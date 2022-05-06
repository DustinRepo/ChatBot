package me.dustin.chatbot.network.key;

import me.dustin.chatbot.network.packet.pipeline.PacketByteBuf;

import java.util.Map;

public record SaltAndSig(long salt, byte[] signature){
    public static final SaltAndSig EMPTY = new SaltAndSig(0L, new byte[0]);
    public static SaltAndSig from(PacketByteBuf buf) {
        return new SaltAndSig(buf.readLong(), buf.readByteArray());
    }

    public void write(PacketByteBuf packetByteBuf) {
        packetByteBuf.writeLong(this.salt());
        packetByteBuf.writeByteArray(this.signature());
    }

    public record SaltAndSigs(long salt, Map<String, byte[]> signatures){
        public static final SaltAndSigs EMPTY = new SaltAndSigs(0L, Map.of());
        public static SaltAndSig from(PacketByteBuf buf) {
            return new SaltAndSig(buf.readLong(), buf.readByteArray());
        }

        public void write(PacketByteBuf packetByteBuf) {
            packetByteBuf.writeLong(this.salt());
            packetByteBuf.writeMap(signatures(), (packetByteBuf1, s) -> packetByteBuf1.writeString(s, 16), PacketByteBuf::writeByteArray);
        }
    }
}
