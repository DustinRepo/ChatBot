package me.dustin.chatbot.network.packet.c2s.login;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import me.dustin.chatbot.network.packet.Packet;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

public class ServerBoundEncryptionResponsePacket extends Packet {
    private final SecretKey secretKey;
    private final byte[] encryptedSecret;
    private final byte[] encryptedVerify;
    public ServerBoundEncryptionResponsePacket(SecretKey secretKey, byte[] encryptedSecret, byte[] encryptedVerify) {
        super(0x01);
        this.secretKey = secretKey;
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
    public ByteArrayDataOutput createPacket() throws IOException {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        ByteArrayOutputStream encryptionResponseBytes = new ByteArrayOutputStream();
        DataOutputStream encryptionResponsePacket = new DataOutputStream(encryptionResponseBytes);

        encryptionResponsePacket.writeByte(this.packetId);

        writeVarInt(encryptionResponsePacket, encryptedSecret.length);
        encryptionResponsePacket.write(encryptedSecret);

        writeVarInt(encryptionResponsePacket, encryptedVerify.length);
        encryptionResponsePacket.write(encryptedVerify);

        writeVarInt(out, encryptionResponseBytes.toByteArray().length);
        out.write(encryptionResponseBytes.toByteArray());
        return out;
    }
}
