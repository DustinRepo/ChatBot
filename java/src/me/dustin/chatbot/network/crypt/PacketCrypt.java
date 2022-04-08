package me.dustin.chatbot.network.crypt;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.*;

public class PacketCrypt {
    private PublicKey publicKey;
    private SecretKey secretKey;

    private Cipher encryptCipher;
    private Cipher decryptCipher;

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

    public InputStream decryptInputStream(InputStream inputStream) {
        return new CipherInputStream(inputStream, decryptCipher);
    }

    public OutputStream encryptOutputStream(OutputStream outputStream) {
        return new CipherOutputStream(outputStream, encryptCipher);
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
