package me.dustin.chatbot.network.packet.impl.query.c2s;

import me.dustin.chatbot.network.packet.Packet;

public class ServerBoundQueryRequestPacket extends Packet {
    public ServerBoundQueryRequestPacket() {
        super(0x00);
    }
}
