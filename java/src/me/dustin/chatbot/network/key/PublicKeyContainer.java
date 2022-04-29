package me.dustin.chatbot.network.key;

import java.time.Instant;

public record PublicKeyContainer(Instant expiresAt, String keyString, String signature) {
}
