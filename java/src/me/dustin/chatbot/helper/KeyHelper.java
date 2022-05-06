package me.dustin.chatbot.helper;

import com.google.common.primitives.Longs;
import me.dustin.chatbot.ChatBot;
import me.dustin.chatbot.network.key.KeyContainer;
import me.dustin.chatbot.network.key.KeyPairResponse;
import me.dustin.chatbot.network.key.PublicKeyContainer;
import me.dustin.chatbot.network.key.SaltAndSig;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.EncodedKeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.time.Instant;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class KeyHelper {
    private static final SecureRandom secureRandom = new SecureRandom();

    public static KeyPairResponse getKeyPairResponse(String accessToken) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json; charset=utf-8");
        headers.put("Content-Length", "0");
        headers.put("Authorization", "Bearer " + accessToken);
        GeneralHelper.HttpResponse httpResponse = GeneralHelper.httpRequest("https://api.minecraftservices.com/player/certificates", null, headers, "POST");
        return GeneralHelper.gson.fromJson(httpResponse.data(), KeyPairResponse.class);
    }

    public static SaltAndSig sigForMessage(Instant instant, String string, PrivateKey privateKey, UUID uuid) {
        try {
            Signature signature = getSignature(privateKey);
            if (signature != null) {
                long l = nextLong();
                updateSig(signature, l, uuid, instant, String.format("{\"text\":\"%s\"}", string));
                return new SaltAndSig(l, signature.sign());
            }
        } catch (GeneralSecurityException var6) {
            System.out.println("Failed to sign chat message");
        }

        return new SaltAndSig(0L, new byte[0]);
    }

    private static void updateSig(Signature signature, long l, UUID uUID, Instant instant, String string) throws SignatureException {
        signature.update(Longs.toByteArray(l));
        signature.update(uuidToBytes(uUID.getMostSignificantBits(), uUID.getLeastSignificantBits()));
        signature.update(Longs.toByteArray(instant.getEpochSecond()));
        signature.update(string.getBytes(StandardCharsets.UTF_8));
    }

    private static byte[] uuidToBytes(long l, long m) {
        ByteBuffer byteBuffer = ByteBuffer.allocate(16).order(ByteOrder.BIG_ENDIAN);
        byteBuffer.putLong(l).putLong(m);
        return byteBuffer.array();
    }

    public static SaltAndSig generateSaltAndSig(Instant instant, String message) {
        return ChatBot.getClientConnection().getKeyContainer() == null ? SaltAndSig.EMPTY : KeyHelper.sigForMessage(instant, message, ChatBot.getClientConnection().getKeyContainer().privateKey(), ChatBot.getClientConnection().getClientPlayer().getUuid());
    }

    public static KeyContainer getKeyContainer(KeyPairResponse keyPairResponse) {
        return new KeyContainer(getPrivateKey(keyPairResponse.getPrivateKey()), new PublicKeyContainer(Instant.parse(keyPairResponse.getExpiresAt()), keyPairResponse.getPublicKey(), keyPairResponse.getPublicKeySignature()), Instant.parse(keyPairResponse.getRefreshedAfter()));
    }

    public static Signature getSignature(PrivateKey privateKey) throws GeneralSecurityException {
        if (privateKey == null) {
            return null;
        } else {
            Signature signature = Signature.getInstance("SHA256withRSA");
            signature.initSign(privateKey);
            return signature;
        }
    }

    public static PublicKey getPublicKey(String s) {
       return decodePublicKey(s);
    }

    public static PrivateKey getPrivateKey(String s) {
        return decodePrivateKey(s);
    }

    private static PrivateKey createKey(byte[] bs) {
        try {
            PKCS8EncodedKeySpec encodedKeySpec = new PKCS8EncodedKeySpec(bs);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return keyFactory.generatePrivate(encodedKeySpec);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static PublicKey createPublicKey(byte[] bs) {
        try {
            X509EncodedKeySpec encodedKeySpec = new X509EncodedKeySpec(bs);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return keyFactory.generatePublic(encodedKeySpec);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static PrivateKey decodePrivateKey(String string) {
        int i = string.indexOf("-----BEGIN RSA PRIVATE KEY-----");
        if (i != -1) {
            i += "-----BEGIN RSA PRIVATE KEY-----".length();
            int j = string.indexOf("-----END RSA PRIVATE KEY-----", i);
            string = string.substring(i, j + 1);
        }

        return createKey(Base64.getMimeDecoder().decode(string));
    }

    private static PublicKey decodePublicKey(String string) {
        int i = string.indexOf("-----BEGIN RSA PUBLIC KEY-----");
        if (i != -1) {
            int j = string.indexOf("-----END RSA PUBLIC KEY-----", i += "-----BEGIN RSA PUBLIC KEY-----".length());
            string = string.substring(i, j + 1);
        }
        return createPublicKey(Base64.getMimeDecoder().decode(string));
    }

    public static long nextLong() {
        return secureRandom.nextLong();
    }

}
