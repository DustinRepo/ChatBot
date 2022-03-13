package me.dustin.chatbot.network.packet.handler;

import me.dustin.chatbot.ChatBot;
import me.dustin.chatbot.chat.ChatMessage;
import me.dustin.chatbot.event.EventLoginSuccess;
import me.dustin.chatbot.helper.GeneralHelper;
import me.dustin.chatbot.network.ClientConnection;
import me.dustin.chatbot.network.packet.c2s.login.ServerBoundEncryptionResponsePacket;
import me.dustin.chatbot.network.packet.c2s.login.ServerBoundPluginResponsePacket;
import me.dustin.chatbot.network.packet.c2s.play.ServerBoundClientSettingsPacket;
import me.dustin.chatbot.network.packet.c2s.play.ServerBoundTabCompletePacket;
import me.dustin.chatbot.network.packet.s2c.login.*;

import javax.crypto.SecretKey;
import java.math.BigInteger;

public class ClientBoundLoginClientBoundPacketHandler extends ClientBoundPacketHandler {

    public ClientBoundLoginClientBoundPacketHandler(ClientConnection clientConnection) {
        super(clientConnection);
        getPacketMap().put(0x00, ClientBoundDisconnectPacket.class);
        getPacketMap().put(0x01, ClientBoundEncryptionStartPacket.class);
        getPacketMap().put(0x02, ClientBoundLoginSuccessPacket.class);
        getPacketMap().put(0x03, ClientBoundSetCompressionPacket.class);
        getPacketMap().put(0x04, ClientBoundPluginRequestPacket.class);
    }

    public void handleEncryptionRequest(ClientBoundEncryptionStartPacket encryptionStartPacket) {
        GeneralHelper.print("Received EncryptionRequest", GeneralHelper.TextColors.GREEN);
        if (encryptionStartPacket.getPublicKey() == null)
            return;
        try {
            SecretKey secretKey = ServerBoundEncryptionResponsePacket.generateSecret();
            getClientConnection().getPacketCrypt().setSecretKey(secretKey);
            getClientConnection().getPacketCrypt().setPublicKey(encryptionStartPacket.getPublicKey());
            getClientConnection().getPacketCrypt().generateCiphers();

            String serverHash = new BigInteger(getClientConnection().getPacketCrypt().hash(encryptionStartPacket.getServerID().getBytes("ISO_8859_1"), getClientConnection().getPacketCrypt().getSecretKey().getEncoded(), getClientConnection().getPacketCrypt().getPublicKey().getEncoded())).toString(16);
            GeneralHelper.print("Contacting Auth Servers...", GeneralHelper.TextColors.GREEN);
            getClientConnection().contactAuthServers(serverHash);

            byte[] encryptedSecret = getClientConnection().getPacketCrypt().encrypt(secretKey.getEncoded());
            byte[] encryptedVerify = getClientConnection().getPacketCrypt().encrypt(encryptionStartPacket.getVerifyToken());

            ServerBoundEncryptionResponsePacket serverBoundEncryptionResponsePacket = new ServerBoundEncryptionResponsePacket(encryptedSecret, encryptedVerify);

            getClientConnection().sendPacket(serverBoundEncryptionResponsePacket);
            GeneralHelper.print("Encrypting connection...", GeneralHelper.TextColors.GREEN);
            getClientConnection().activateEncryption();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void handleCompressionPacket(ClientBoundSetCompressionPacket clientBoundSetCompressionPacket) {
        GeneralHelper.print("Setting compression threshold to: " + clientBoundSetCompressionPacket.getCompressionThreshold(), GeneralHelper.TextColors.GREEN);
        getClientConnection().setCompressionThreshold(clientBoundSetCompressionPacket.getCompressionThreshold());
    }

    public void handlePluginRequestPacket(ClientBoundPluginRequestPacket clientBoundPluginRequestPacket) {
        int id = clientBoundPluginRequestPacket.getMessageId();
        GeneralHelper.print("Received Plugin Request packet " + id + " " + clientBoundPluginRequestPacket.getIdentifier(), GeneralHelper.TextColors.GREEN);
        getClientConnection().sendPacket(new ServerBoundPluginResponsePacket(id));
    }

    public void handleLoginSuccess(ClientBoundLoginSuccessPacket clientBoundLoginSuccessPacket) {
        getClientConnection().setNetworkState(ClientConnection.NetworkState.PLAY);
        getClientConnection().setClientBoundPacketHandler(new ClientBoundPlayClientBoundPacketHandler(getClientConnection()));
        getClientConnection().getClientPlayer().updateKeepAlive();
        getClientConnection().getClientPlayer().updateAntiAFK();
        getClientConnection().getTpsHelper().clear();
        GeneralHelper.print("Login Success Packet. You are connected", GeneralHelper.TextColors.GREEN);
        GeneralHelper.print("Setting NETWORK_STATE to PLAY", GeneralHelper.TextColors.GREEN);
        new EventLoginSuccess().run(getClientConnection());
    }

    public void handleDisconnectPacket(ClientBoundDisconnectPacket clientBoundDisconnectPacket) {
        GeneralHelper.print("Disconnected: " + ChatMessage.of(clientBoundDisconnectPacket.getReason()).getMessage(), GeneralHelper.TextColors.RED);
        getClientConnection().close();
    }
}
