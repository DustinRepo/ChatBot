package me.dustin.chatbot.network.packet.c2s.query;

import me.dustin.chatbot.network.packet.Packet;

public class ServerBoundQueryRequestPacket extends Packet {
    public ServerBoundQueryRequestPacket() {
        super(0x00);
    }
}
