package me.dustin.chatbot.network.packet.impl.play.s2c;

import me.dustin.chatbot.network.packet.Packet;
import me.dustin.chatbot.network.packet.handler.ClientBoundPacketHandler;
import me.dustin.chatbot.network.packet.handler.PlayClientBoundPacketHandler;
import me.dustin.chatbot.network.packet.pipeline.PacketByteBuf;

public class ClientBoundPingPacket extends Packet.ClientBoundPacket {
    private final int id;

    public ClientBoundPingPacket(PacketByteBuf packetByteBuf) {
        super(packetByteBuf);
        this.id = packetByteBuf.readInt();
    }

    @Override
    public void handlePacket(ClientBoundPacketHandler clientBoundPacketHandler) {
        ((PlayClientBoundPacketHandler)clientBoundPacketHandler).handlePingPacket(this);
    }

    public int getId() {
        return id;
    }
}
