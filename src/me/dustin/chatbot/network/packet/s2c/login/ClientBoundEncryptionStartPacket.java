package me.dustin.chatbot.network.packet.s2c.login;

import me.dustin.chatbot.network.ClientBoundPacketHandler;
import me.dustin.chatbot.network.packet.Packet;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;

public class ClientBoundEncryptionStartPacket extends Packet.ClientBoundPacket {

    public String serverID = "";
    public PublicKey publicKey;
    public byte[] verifyToken;

    public ClientBoundEncryptionStartPacket(ClientBoundPacketHandler clientBoundPacketHandler) {
        super(0x01, clientBoundPacketHandler);
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
        clientBoundPacketHandler.handleEncryptionRequest(this);
    }
}
