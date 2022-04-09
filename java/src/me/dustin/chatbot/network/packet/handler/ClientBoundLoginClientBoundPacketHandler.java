package me.dustin.chatbot.network.packet.handler;

import me.dustin.chatbot.chat.ChatMessage;
import me.dustin.chatbot.event.EventLoginSuccess;
import me.dustin.chatbot.helper.GeneralHelper;
import me.dustin.chatbot.network.ClientConnection;
import me.dustin.chatbot.network.packet.c2s.login.ServerBoundEncryptionResponsePacket;
import me.dustin.chatbot.network.packet.c2s.login.ServerBoundPluginResponsePacket;
import me.dustin.chatbot.network.packet.s2c.login.*;

import javax.crypto.SecretKey;
import java.math.BigInteger;

public class ClientBoundLoginClientBoundPacketHandler extends ClientBoundPacketHandler {

    public void handleEncryptionRequest(ClientBoundEncryptionStartPacket encryptionStartPacket) {
        GeneralHelper.print("Received EncryptionRequest", ChatMessage.TextColors.GREEN);
        if (encryptionStartPacket.getPublicKey() == null)
            return;
        try {
            SecretKey secretKey = ServerBoundEncryptionResponsePacket.generateSecret();
            getClientConnection().getPacketCrypt().setSecretKey(secretKey);
            getClientConnection().getPacketCrypt().setPublicKey(encryptionStartPacket.getPublicKey());
            getClientConnection().getPacketCrypt().generateCiphers();

            String serverHash = new BigInteger(getClientConnection().getPacketCrypt().hash(encryptionStartPacket.getServerID().getBytes("ISO_8859_1"), getClientConnection().getPacketCrypt().getSecretKey().getEncoded(), getClientConnection().getPacketCrypt().getPublicKey().getEncoded())).toString(16);
            GeneralHelper.print("Contacting Auth Servers...", ChatMessage.TextColors.GREEN);
            if (getClientConnection().contactAuthServers(serverHash)) {
                byte[] encryptedSecret = getClientConnection().getPacketCrypt().encrypt(secretKey.getEncoded());
                byte[] encryptedVerify = getClientConnection().getPacketCrypt().encrypt(encryptionStartPacket.getVerifyToken());

                ServerBoundEncryptionResponsePacket serverBoundEncryptionResponsePacket = new ServerBoundEncryptionResponsePacket(encryptedSecret, encryptedVerify);

                getClientConnection().sendPacket(serverBoundEncryptionResponsePacket);
                GeneralHelper.print("Encrypting connection...", ChatMessage.TextColors.GREEN);
                getClientConnection().activateEncryption();
            } else {
                GeneralHelper.print("Error! Could not verify with auth servers", ChatMessage.TextColors.DARK_RED);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void handleCompressionPacket(ClientBoundSetCompressionPacket clientBoundSetCompressionPacket) {
        GeneralHelper.print("Setting compression threshold to: " + clientBoundSetCompressionPacket.getCompressionThreshold(), ChatMessage.TextColors.GREEN);
        getClientConnection().setCompressionThreshold(clientBoundSetCompressionPacket.getCompressionThreshold());
    }

    public void handlePluginRequestPacket(ClientBoundPluginRequestPacket clientBoundPluginRequestPacket) {
        int id = clientBoundPluginRequestPacket.getMessageId();
        GeneralHelper.print("Received Plugin Request packet " + id + " " + clientBoundPluginRequestPacket.getIdentifier(), ChatMessage.TextColors.GREEN);
        getClientConnection().sendPacket(new ServerBoundPluginResponsePacket(id));
    }

    public void handleLoginSuccess(ClientBoundLoginSuccessPacket clientBoundLoginSuccessPacket) {
        getClientConnection().setNetworkState(ClientConnection.NetworkState.PLAY);
        getClientConnection().setClientBoundPacketHandler(new ClientBoundPlayClientBoundPacketHandler());
        getClientConnection().getTpsHelper().clear();
        GeneralHelper.print("Login Success Packet. You are connected.", ChatMessage.TextColors.GREEN);
        GeneralHelper.print("Username: " + clientBoundLoginSuccessPacket.getUsername(), ChatMessage.TextColors.GOLD);
        new EventLoginSuccess().run(getClientConnection());
    }

    public void handleDisconnectPacket(ClientBoundDisconnectLoginPacket clientBoundDisconnectLoginPacket) {
        GeneralHelper.print("Disconnected", ChatMessage.TextColors.DARK_RED);
        GeneralHelper.printChat(ChatMessage.of(clientBoundDisconnectLoginPacket.getReason()));
    }

}
