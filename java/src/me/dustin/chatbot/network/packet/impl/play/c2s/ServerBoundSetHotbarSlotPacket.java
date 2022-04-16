package me.dustin.chatbot.network.packet.impl.play.c2s;

import me.dustin.chatbot.network.packet.Packet;
import me.dustin.chatbot.network.ProtocolHandler;
import me.dustin.chatbot.network.packet.pipeline.PacketByteBuf;

import java.io.IOException;

public class ServerBoundSetHotbarSlotPacket extends Packet {
    private final short slot;

    public ServerBoundSetHotbarSlotPacket(short slot) {
        super(ProtocolHandler.getCurrent().getPacketId(ProtocolHandler.NetworkSide.SERVERBOUND, "hotbar_slot"));
        this.slot = slot;
    }

    @Override
    public void createPacket(PacketByteBuf packetByteBuf) throws IOException {
        packetByteBuf.writeShort(slot);
    }
}
