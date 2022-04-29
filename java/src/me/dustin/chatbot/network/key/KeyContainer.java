package me.dustin.chatbot.network.key;

import java.security.PrivateKey;
import java.time.Instant;

public record KeyContainer(PrivateKey privateKey, PublicKeyContainer publicKey, Instant refreshedAfter) {
}
