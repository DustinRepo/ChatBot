package me.dustin.chatbot.network.packet.s2c.login;

import me.dustin.chatbot.network.packet.pipeline.PacketByteBuf;
import me.dustin.chatbot.network.packet.Packet;
import me.dustin.chatbot.network.packet.handler.LoginClientBoundPacketHandler;
import me.dustin.chatbot.network.packet.handler.ClientBoundPacketHandler;

public class ClientBoundSetCompressionPacket extends Packet.ClientBoundPacket {
    private final int compressionThreshold;
    public ClientBoundSetCompressionPacket(PacketByteBuf packetByteBuf) {
        super(packetByteBuf);
        this.compressionThreshold = packetByteBuf.readVarInt();
    }

    public int getCompressionThreshold() {
        return compressionThreshold;
    }

    @Override
    public void apply(ClientBoundPacketHandler clientBoundPacketHandler) {
        ((LoginClientBoundPacketHandler)clientBoundPacketHandler).handleCompressionPacket(this);
    }
}
