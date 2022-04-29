package me.dustin.chatbot.network.key;

public class KeyPairResponse {
    private KeyPair keyPair;
    private String publicKeySignature;
    private String expiresAt;
    private String refreshedAfter;

    public String getPrivateKey() {
        return keyPair.privateKey;
    }

    public String getPublicKey() {
        return keyPair.publicKey;
    }

    public String getPublicKeySignature() {
        return publicKeySignature;
    }

    public String getExpiresAt() {
        return expiresAt;
    }

    public String getRefreshedAfter() {
        return refreshedAfter;
    }

    private static final class KeyPair {
        private String privateKey;
        private String publicKey;
    }
}
