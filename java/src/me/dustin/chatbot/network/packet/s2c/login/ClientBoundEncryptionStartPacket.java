package me.dustin.chatbot.network.packet.s2c.login;

import me.dustin.chatbot.ChatBot;
import me.dustin.chatbot.network.Protocols;
import me.dustin.chatbot.network.packet.pipeline.PacketByteBuf;
import me.dustin.chatbot.network.packet.Packet;
import me.dustin.chatbot.network.packet.handler.ClientBoundLoginClientBoundPacketHandler;
import me.dustin.chatbot.network.packet.handler.ClientBoundPacketHandler;

import java.io.IOException;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;

public class ClientBoundEncryptionStartPacket extends Packet.ClientBoundPacket {

    private String serverID = "";
    private PublicKey publicKey;
    private byte[] verifyToken;

    public ClientBoundEncryptionStartPacket(ClientBoundPacketHandler clientBoundPacketHandler) {
        super(0x01, clientBoundPacketHandler);
    }

    @Override
    public void createPacket(PacketByteBuf packetByteBuf) throws IOException {
        int publicKeyLength;
        int verifyTokenLength;


        this.serverID = packetByteBuf.readString();

        //public key
        publicKeyLength = ChatBot.getConfig().getProtocolVersion() <= Protocols.V1_7_10.getProtocolVer() ? packetByteBuf.readShort() : packetByteBuf.readVarInt();
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
        verifyTokenLength = ChatBot.getConfig().getProtocolVersion() <= Protocols.V1_7_10.getProtocolVer() ? packetByteBuf.readShort() : packetByteBuf.readVarInt();
        this.verifyToken = new byte[verifyTokenLength];
        packetByteBuf.readBytes(this.verifyToken, 0, verifyTokenLength);
    }

    @Override
    public void apply() {
        ((ClientBoundLoginClientBoundPacketHandler)clientBoundPacketHandler).handleEncryptionRequest(this);
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
