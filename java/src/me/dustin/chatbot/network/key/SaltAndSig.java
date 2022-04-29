package me.dustin.chatbot.network.key;

import me.dustin.chatbot.network.packet.pipeline.PacketByteBuf;

public record SaltAndSig(long salt, byte[] signature){
    public static SaltAndSig from(PacketByteBuf buf) {
        return new SaltAndSig(buf.readLong(), buf.readByteArray());
    }
}
