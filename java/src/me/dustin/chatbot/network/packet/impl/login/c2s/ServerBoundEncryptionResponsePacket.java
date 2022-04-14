package me.dustin.chatbot.network.packet.impl.login.c2s;

import me.dustin.chatbot.network.packet.Packet;
import me.dustin.chatbot.network.packet.ProtocolHandler;
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
        if (ProtocolHandler.getCurrent().getProtocolVer() <= ProtocolHandler.getVersionFromName("1.7.10").getProtocolVer())
            packetByteBuf.writeShort(encryptedSecret.length);
        else
            packetByteBuf.writeVarInt(encryptedSecret.length);
        packetByteBuf.writeBytes(encryptedSecret);

        if (ProtocolHandler.getCurrent().getProtocolVer() <= ProtocolHandler.getVersionFromName("1.7.10").getProtocolVer())
            packetByteBuf.writeShort(encryptedVerify.length);
        else
            packetByteBuf.writeVarInt(encryptedVerify.length);
        packetByteBuf.writeBytes(encryptedVerify);
    }
}
