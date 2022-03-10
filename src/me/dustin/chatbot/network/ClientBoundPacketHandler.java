package me.dustin.chatbot.network;

import me.dustin.chatbot.ChatBot;
import me.dustin.chatbot.helper.GeneralHelper;
import me.dustin.chatbot.helper.MessageParser;
import me.dustin.chatbot.network.packet.c2s.login.ServerBoundEncryptionResponsePacket;
import me.dustin.chatbot.network.packet.c2s.login.ServerBoundPluginResponsePacket;
import me.dustin.chatbot.network.packet.c2s.play.ServerBoundChatPacket;
import me.dustin.chatbot.network.packet.c2s.play.ServerBoundClientStatusPacket;
import me.dustin.chatbot.network.packet.c2s.play.ServerBoundKeepAlivePacket;
import me.dustin.chatbot.network.packet.s2c.login.*;
import me.dustin.chatbot.network.packet.s2c.play.*;
import me.dustin.chatbot.network.player.OtherPlayer;
import me.dustin.chatbot.network.player.PlayerManager;

import javax.crypto.SecretKey;
import java.math.BigInteger;

public class ClientBoundPacketHandler {

    private ClientConnection clientConnection;

    public ClientBoundPacketHandler(ClientConnection clientConnection) {
        this.clientConnection = clientConnection;
    }

    public void handleEncryptionRequest(ClientBoundEncryptionStartPacket encryptionStartPacket) {
        GeneralHelper.print("Received EncryptionRequest", GeneralHelper.ANSI_GREEN);
        if (encryptionStartPacket.getPublicKey() == null)
            return;
        try {
            SecretKey secretKey = ServerBoundEncryptionResponsePacket.generateSecret();
            clientConnection.getPacketCrypt().setSecretKey(secretKey);
            clientConnection.getPacketCrypt().setPublicKey(encryptionStartPacket.getPublicKey());
            clientConnection.getPacketCrypt().generateCiphers();

            String serverHash = new BigInteger(clientConnection.getPacketCrypt().hash(encryptionStartPacket.getServerID().getBytes("ISO_8859_1"), clientConnection.getPacketCrypt().getSecretKey().getEncoded(), clientConnection.getPacketCrypt().getPublicKey().getEncoded())).toString(16);
            GeneralHelper.print("Contacting Auth Servers", GeneralHelper.ANSI_GREEN);
            clientConnection.contactAuthServers(serverHash);

            byte[] encryptedSecret = clientConnection.getPacketCrypt().encrypt(secretKey.getEncoded());
            byte[] encryptedVerify = clientConnection.getPacketCrypt().encrypt(encryptionStartPacket.getVerifyToken());

            ServerBoundEncryptionResponsePacket serverBoundEncryptionResponsePacket = new ServerBoundEncryptionResponsePacket(secretKey, encryptedSecret, encryptedVerify);

            clientConnection.sendPacket(serverBoundEncryptionResponsePacket);
            GeneralHelper.print("Encrypting connection", GeneralHelper.ANSI_GREEN);
            clientConnection.activateEncryption();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void handleCompressionPacket(ClientBoundSetCompressionPacket clientBoundSetCompressionPacket) {
        GeneralHelper.print("Setting compression threshold to: " + clientBoundSetCompressionPacket.getCompressionThreshold(), GeneralHelper.ANSI_GREEN);
        this.clientConnection.setCompressionThreshold(clientBoundSetCompressionPacket.getCompressionThreshold());
    }

    public void handlePluginRequestPacket(ClientBoundPluginRequestPacket clientBoundPluginRequestPacket) {
        int id = clientBoundPluginRequestPacket.getMessageId();
        GeneralHelper.print("Received Plugin Request packet " + id + " " + clientBoundPluginRequestPacket.getIdentifier(), GeneralHelper.ANSI_GREEN);
        clientConnection.sendPacket(new ServerBoundPluginResponsePacket(id));
    }

    public void handleLoginSuccess(ClientBoundLoginSuccessPacket clientBoundLoginSuccessPacket) {
        clientConnection.setNetworkState(ClientConnection.NetworkState.PLAY);
        clientConnection.getTpsHelper().clear();
        GeneralHelper.print("Login Success Packet. You are connected", GeneralHelper.ANSI_GREEN);
        GeneralHelper.print("Setting NETWORK_STATE to PLAY", GeneralHelper.ANSI_GREEN);
    }

    public void handleDisconnectPacket(ClientBoundDisconnectPacket clientBoundDisconnectPacket) {
        GeneralHelper.print("Disconnected: " + clientBoundDisconnectPacket.getReason(), GeneralHelper.ANSI_RED);
        clientConnection.close();
    }

    public void handleDisconnectPacket(ClientBoundDisconnectPlayPacket clientBoundDisconnectPacket) {
        GeneralHelper.print("Disconnected: " + clientBoundDisconnectPacket.getReason(), GeneralHelper.ANSI_RED);
        clientConnection.close();
    }

    public void handleKeepAlive(ClientBoundKeepAlivePacket keepAlivePacket) {
        //send KeepAlive packet back with same ID
        long id = keepAlivePacket.getId();
        clientConnection.sendPacket(new ServerBoundKeepAlivePacket(id));
    }

    public void handleChatMessage(ClientBoundChatMessagePacket clientBoundChatMessagePacket) {
        String message = MessageParser.INSTANCE.parse(clientBoundChatMessagePacket.getMessage());
        GeneralHelper.print(message, GeneralHelper.ANSI_CYAN);
        if (!clientConnection.getCommandManager().parse(MessageParser.INSTANCE.parse(clientBoundChatMessagePacket.getMessage())) && ChatBot.getConfig().isCrackedLogin()) {
            if (message.contains("/register")) {
                clientConnection.sendPacket(new ServerBoundChatPacket("/register " + ChatBot.getConfig().getCrackedLoginPassword() + " " + ChatBot.getConfig().getCrackedLoginPassword()));
            } else if (message.contains("/login")) {
                clientConnection.sendPacket(new ServerBoundChatPacket("/login " + ChatBot.getConfig().getCrackedLoginPassword()));
            }
        }
    }

    public void handlePlayerInfoPacket(ClientBoundPlayerInfoPacket clientBoundPlayerInfoPacket) {
        for (OtherPlayer player : clientBoundPlayerInfoPacket.getPlayers()) {
            switch (clientBoundPlayerInfoPacket.getAction()) {
                case ClientBoundPlayerInfoPacket.ADD_PLAYER -> {
                    PlayerManager.INSTANCE.getPlayers().add(player);
                    GeneralHelper.print("Added player " + player.uuid + " " + player.name, GeneralHelper.ANSI_YELLOW);
                }
                case ClientBoundPlayerInfoPacket.REMOVE_PLAYER -> {
                    if (player != null) {
                        GeneralHelper.print("Removed player " + player.name, GeneralHelper.ANSI_YELLOW);
                        PlayerManager.INSTANCE.getPlayers().remove(player);
                    }
                }
            }
        }
    }

    public void handleWorldTimePacket(ClientBoundWorldTimePacket clientBoundWorldTimePacket) {
        clientConnection.getTpsHelper().worldTime();
    }

    public void handleUpdateHealthPacket(ClientBoundUpdateHealthPacket clientBoundUpdateHealthPacket) {
        if (clientBoundUpdateHealthPacket.getHealth() <= 0) {
            clientConnection.sendPacket(new ServerBoundClientStatusPacket(ServerBoundClientStatusPacket.RESPAWN));
        }
    }

    public void handlePlayerDeadPacket(ClientBoundPlayerDeadPacket clientBoundPlayerDeadPacket) {
        clientConnection.sendPacket(new ServerBoundClientStatusPacket(ServerBoundClientStatusPacket.RESPAWN));
    }
}
