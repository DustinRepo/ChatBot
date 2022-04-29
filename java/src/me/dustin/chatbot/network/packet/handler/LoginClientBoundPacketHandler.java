package me.dustin.chatbot.network.packet.handler;

import me.dustin.chatbot.chat.ChatMessage;
import me.dustin.chatbot.event.EventLoginSuccess;
import me.dustin.chatbot.helper.GeneralHelper;
import me.dustin.chatbot.network.ClientConnection;
import me.dustin.chatbot.network.packet.impl.login.c2s.ServerBoundEncryptionResponsePacket;
import me.dustin.chatbot.network.packet.impl.login.c2s.ServerBoundPluginResponsePacket;
import me.dustin.chatbot.network.packet.impl.login.s2c.*;

import javax.crypto.SecretKey;
import java.math.BigInteger;

public class LoginClientBoundPacketHandler extends ClientBoundPacketHandler {

    public void handleEncryptionRequest(ClientBoundEncryptionStartPacket encryptionStartPacket) {
        GeneralHelper.print("Received EncryptionRequest", ChatMessage.TextColor.GREEN);
        if (encryptionStartPacket.getPublicKey() == null)
            return;
        try {
            SecretKey secretKey = ServerBoundEncryptionResponsePacket.generateSecret();
            getClientConnection().getPacketCrypt().setSecretKey(secretKey);
            getClientConnection().getPacketCrypt().setPublicKey(encryptionStartPacket.getPublicKey());
            getClientConnection().getPacketCrypt().generateCiphers();

            String serverHash = new BigInteger(getClientConnection().getPacketCrypt().hash(encryptionStartPacket.getServerID().getBytes("ISO_8859_1"), getClientConnection().getPacketCrypt().getSecretKey().getEncoded(), getClientConnection().getPacketCrypt().getPublicKey().getEncoded())).toString(16);
            GeneralHelper.print("Contacting Auth Servers...", ChatMessage.TextColor.GREEN);
            if (!getClientConnection().contactSessionServers(serverHash)) {
                GeneralHelper.print("Error! Could not verify with auth servers", ChatMessage.TextColor.DARK_RED);
                return;
            }
            byte[] encryptedSecret = getClientConnection().getPacketCrypt().encrypt(secretKey.getEncoded());
            byte[] encryptedVerify = getClientConnection().getPacketCrypt().encrypt(encryptionStartPacket.getVerifyToken());

            ServerBoundEncryptionResponsePacket serverBoundEncryptionResponsePacket = new ServerBoundEncryptionResponsePacket(encryptedSecret, encryptedVerify);

            getClientConnection().sendPacket(serverBoundEncryptionResponsePacket);
            GeneralHelper.print("Encrypting connection...", ChatMessage.TextColor.GREEN);
            getClientConnection().activateEncryption();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void handleCompressionPacket(ClientBoundSetCompressionPacket clientBoundSetCompressionPacket) {
        GeneralHelper.print("Setting compression threshold to: " + clientBoundSetCompressionPacket.getCompressionThreshold(), ChatMessage.TextColor.GREEN);
        getClientConnection().setCompressionThreshold(clientBoundSetCompressionPacket.getCompressionThreshold());
    }

    public void handlePluginRequestPacket(ClientBoundPluginRequestPacket clientBoundPluginRequestPacket) {
        int id = clientBoundPluginRequestPacket.getMessageId();
        GeneralHelper.print("Received Plugin Request packet " + clientBoundPluginRequestPacket.getIdentifier(), ChatMessage.TextColor.GREEN);
        getClientConnection().sendPacket(new ServerBoundPluginResponsePacket(id));
    }

    public void handleLoginSuccess(ClientBoundLoginSuccessPacket clientBoundLoginSuccessPacket) {
        getClientConnection().setNetworkState(ClientConnection.NetworkState.PLAY);
        getClientConnection().setClientBoundPacketHandler(new PlayClientBoundPacketHandler());
        getClientConnection().getTpsHelper().clear();
        getClientConnection().getClientPlayer().setName(clientBoundLoginSuccessPacket.getUsername());
        getClientConnection().getClientPlayer().setUuid(clientBoundLoginSuccessPacket.getUuid());
        GeneralHelper.print("Login Success Packet. You are connected.", ChatMessage.TextColor.GREEN);
        GeneralHelper.print("Username: " + clientBoundLoginSuccessPacket.getUsername(), ChatMessage.TextColor.GOLD);
        new EventLoginSuccess().run(getClientConnection());
    }

    public void handleDisconnectPacket(ClientBoundDisconnectLoginPacket clientBoundDisconnectLoginPacket) {
        GeneralHelper.print("Disconnected", ChatMessage.TextColor.DARK_RED);
        GeneralHelper.printChat(ChatMessage.of(clientBoundDisconnectLoginPacket.getReason()));
    }

}
