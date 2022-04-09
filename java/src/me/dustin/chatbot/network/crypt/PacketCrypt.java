package me.dustin.chatbot.network.crypt;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import java.security.*;

public class PacketCrypt {
    private PublicKey publicKey;
    private SecretKey secretKey;

    private Cipher encryptCipher;
    private Cipher decryptCipher;

    private byte[] conversionBytes = new byte[0];
    private byte[] encryptionBytes = new byte[0];

    public byte[] encrypt(byte[] bytes) {
        try {
            String algorithm = publicKey.getAlgorithm();
            Cipher cipher = Cipher.getInstance(algorithm);
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            return cipher.doFinal(bytes);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private byte[] toByteArray(ByteBuf buf) {
        int i = buf.readableBytes();
        if (this.conversionBytes.length < i) {
            this.conversionBytes = new byte[i];
        }

        buf.readBytes(this.conversionBytes, 0, i);
        return this.conversionBytes;
    }

    public ByteBuf decryptPacket(ChannelHandlerContext context, ByteBuf buf) throws ShortBufferException {
        int i = buf.readableBytes();
        byte[] bs = this.toByteArray(buf);
        ByteBuf byteBuf = context.alloc().heapBuffer(this.decryptCipher.getOutputSize(i));
        byteBuf.writerIndex(this.decryptCipher.update(bs, 0, i, byteBuf.array(), byteBuf.arrayOffset()));
        return byteBuf;
    }

    public void encryptPacket(ByteBuf buf, ByteBuf result) throws ShortBufferException {
        int i = buf.readableBytes();
        byte[] bs = this.toByteArray(buf);
        int j = this.encryptCipher.getOutputSize(i);
        if (this.encryptionBytes.length < j) {
            this.encryptionBytes = new byte[j];
        }

        result.writeBytes(this.encryptionBytes, 0, this.encryptCipher.update(bs, 0, i, this.encryptionBytes));
    }

    public void generateCiphers() {
        try {
            String alg = "AES/CFB8/NoPadding";
            encryptCipher = Cipher.getInstance(alg);
            encryptCipher.init(Cipher.ENCRYPT_MODE, secretKey, new IvParameterSpec(secretKey.getEncoded()));

            decryptCipher = Cipher.getInstance(alg);
            decryptCipher.init(Cipher.DECRYPT_MODE, secretKey, new IvParameterSpec(secretKey.getEncoded()));
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        }
    }

    public byte[] hash(byte[] ... bytes) throws Exception {
        MessageDigest messageDigest = MessageDigest.getInstance("SHA-1");
        for (byte[] bs : bytes) {
            messageDigest.update(bs);
        }
        return messageDigest.digest();
    }

    public PublicKey getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(PublicKey publicKey) {
        this.publicKey = publicKey;
    }

    public SecretKey getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(SecretKey secretKey) {
        this.secretKey = secretKey;
    }
}
