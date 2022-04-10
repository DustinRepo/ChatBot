package me.dustin.chatbot.network.packet.c2s.login;

import me.dustin.chatbot.network.Protocols;
import me.dustin.chatbot.network.packet.Packet;
import me.dustin.chatbot.network.packet.pipeline.PacketByteBuf;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

public class ServerBoundEncryptionResponsePacket extends Packet {
    private final byte[] encryptedSecret;
    private final byte[] encryptedVerify;

    public ServerBoundEncryptionResponsePacket(byte[] encryptedSecret, byte[] encryptedVerify) {
        super(0x01);
        this.encryptedSecret = encryptedSecret;
        this.encryptedVerify = encryptedVerify;
    }

    public static SecretKey generateSecret() {
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
            keyGenerator.init(128);
            return keyGenerator.generateKey();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void createPacket(PacketByteBuf packetByteBuf) throws IOException {
        if (Protocols.getCurrent().getProtocolVer() <= Protocols.V1_7_10.getProtocolVer())
            packetByteBuf.writeShort(encryptedSecret.length);
        else
            packetByteBuf.writeVarInt(encryptedSecret.length);
        packetByteBuf.writeBytes(encryptedSecret);

        if (Protocols.getCurrent().getProtocolVer() <= Protocols.V1_7_10.getProtocolVer())
            packetByteBuf.writeShort(encryptedVerify.length);
        else
            packetByteBuf.writeVarInt(encryptedVerify.length);
        packetByteBuf.writeBytes(encryptedVerify);
    }
}
