package me.dustin.chatbot.network.packet.s2c.login;

import me.dustin.chatbot.network.packet.Packet;
import me.dustin.chatbot.network.packet.handler.ClientBoundLoginClientBoundPacketHandler;
import me.dustin.chatbot.network.packet.handler.ClientBoundPacketHandler;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;

public class ClientBoundEncryptionStartPacket extends Packet.ClientBoundPacket {

    private String serverID = "";
    private PublicKey publicKey;
    private byte[] verifyToken;

    public ClientBoundEncryptionStartPacket(ClientBoundPacketHandler clientBoundPacketHandler) {
        super(clientBoundPacketHandler);
    }

    @Override
    public void createPacket(ByteArrayInputStream byteArrayInputStream) throws IOException {
        DataInputStream dataInputStream = new DataInputStream(byteArrayInputStream);
        int publicKeyLength;
        int verifyTokenLength;


        serverID = readString(dataInputStream);

        //public key
        publicKeyLength = readVarInt(dataInputStream);
        byte[] publicKey = new byte[publicKeyLength];
        dataInputStream.readFully(publicKey, 0, publicKeyLength);

        try {
            X509EncodedKeySpec encodedKeySpec = new X509EncodedKeySpec(publicKey);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            this.publicKey = keyFactory.generatePublic(encodedKeySpec);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //verifyToken
        verifyTokenLength = readVarInt(dataInputStream);
        verifyToken = new byte[verifyTokenLength];
        dataInputStream.readFully(verifyToken, 0, verifyTokenLength);

        super.createPacket(byteArrayInputStream);
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
