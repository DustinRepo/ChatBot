package me.dustin.chatbot.network.packet.impl.play.s2c;

import me.dustin.chatbot.network.packet.Packet;
import me.dustin.chatbot.network.packet.handler.ClientBoundPacketHandler;
import me.dustin.chatbot.network.packet.handler.PlayClientBoundPacketHandler;
import me.dustin.chatbot.network.packet.pipeline.PacketByteBuf;

public class ClientBoundSetHotbarSlotPacket extends Packet.ClientBoundPacket {
    private final byte slot;
    public ClientBoundSetHotbarSlotPacket(PacketByteBuf packetByteBuf) {
        super(packetByteBuf);
        this.slot = packetByteBuf.readByte();
    }

    @Override
    public void handlePacket(ClientBoundPacketHandler clientBoundPacketHandler) {
        ((PlayClientBoundPacketHandler)clientBoundPacketHandler).handleSetHotbarSlotPacket(this);
    }

    public byte getSlot() {
        return slot;
    }
}
