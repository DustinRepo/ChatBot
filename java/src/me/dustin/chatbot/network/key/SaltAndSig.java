package me.dustin.chatbot.network.key;

import me.dustin.chatbot.network.packet.pipeline.PacketByteBuf;

public record SaltAndSig(long salt, byte[] signature){
    public static final SaltAndSig EMPTY = new SaltAndSig(0L, new byte[0]);
    public static SaltAndSig from(PacketByteBuf buf) {
        return new SaltAndSig(buf.readLong(), buf.readByteArray());
    }

    public void write(PacketByteBuf packetByteBuf) {
        packetByteBuf.writeLong(this.salt());
        packetByteBuf.writeByteArray(this.signature());
    }
}
