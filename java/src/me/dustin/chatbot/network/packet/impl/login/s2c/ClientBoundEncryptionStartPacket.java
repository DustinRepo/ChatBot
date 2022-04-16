package me.dustin.chatbot.network.packet.impl.login.s2c;

import me.dustin.chatbot.network.ProtocolHandler;
import me.dustin.chatbot.network.packet.pipeline.PacketByteBuf;
import me.dustin.chatbot.network.packet.Packet;
import me.dustin.chatbot.network.packet.handler.LoginClientBoundPacketHandler;
import me.dustin.chatbot.network.packet.handler.ClientBoundPacketHandler;

import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;

public class ClientBoundEncryptionStartPacket extends Packet.ClientBoundPacket {

    private final String serverID;
    private PublicKey publicKey;
    private final byte[] verifyToken;

    public ClientBoundEncryptionStartPacket(PacketByteBuf packetByteBuf) {
        super(packetByteBuf);int publicKeyLength;
        int verifyTokenLength;


        this.serverID = packetByteBuf.readString();

        //public key
        publicKeyLength = ProtocolHandler.getCurrent().getProtocolVer() <= ProtocolHandler.getVersionFromName("1.7.10").getProtocolVer() ? packetByteBuf.readShort() : packetByteBuf.readVarInt();
        byte[] publicKey = new byte[publicKeyLength];
        packetByteBuf.readBytes(publicKey, 0, publicKeyLength);

        try {
            X509EncodedKeySpec encodedKeySpec = new X509EncodedKeySpec(publicKey);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            this.publicKey = keyFactory.generatePublic(encodedKeySpec);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //verifyToken
        verifyTokenLength = ProtocolHandler.getCurrent().getProtocolVer() <= ProtocolHandler.getVersionFromName("1.7.10").getProtocolVer() ? packetByteBuf.readShort() : packetByteBuf.readVarInt();
        this.verifyToken = new byte[verifyTokenLength];
        packetByteBuf.readBytes(this.verifyToken, 0, verifyTokenLength);
    }

    @Override
    public void handlePacket(ClientBoundPacketHandler clientBoundPacketHandler) {
        ((LoginClientBoundPacketHandler)clientBoundPacketHandler).handleEncryptionRequest(this);
    }

    public String getServerID() {
        return serverID;
    }

    public PublicKey getPublicKey() {
        return publicKey;
    }

    public byte[] getVerifyToken() {
        return verifyToken;
    }
}
