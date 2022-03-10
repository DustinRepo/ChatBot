package me.dustin.chatbot.network.crypt;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.SecretKey;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.PublicKey;

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

    public byte[] decrypt(byte[] bytes) {
        try {
            String algorithm = secretKey.getAlgorithm();
            Cipher cipher = Cipher.getInstance(algorithm);
            cipher.init(Cipher.DECRYPT_MODE, publicKey);
            return cipher.doFinal(bytes);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
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

    public Cipher getEncryptCipher() {
        return encryptCipher;
    }

    public void setEncryptCipher(Cipher encryptCipher) {
        this.encryptCipher = encryptCipher;
    }

    public Cipher getDecryptCipher() {
        return decryptCipher;
    }

    public void setDecryptCipher(Cipher decryptCipher) {
        this.decryptCipher = decryptCipher;
    }
}
