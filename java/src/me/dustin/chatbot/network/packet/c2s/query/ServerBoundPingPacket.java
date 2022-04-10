package me.dustin.chatbot.network.packet.c2s.query;

import me.dustin.chatbot.network.packet.Packet;
import me.dustin.chatbot.network.packet.pipeline.PacketByteBuf;

import java.io.IOException;

public class ServerBoundPingPacket extends Packet {

    private final long time;

    public ServerBoundPingPacket(long time) {
        super(0x01);
        this.time = time;
    }

    @Override
    public void createPacket(PacketByteBuf packetByteBuf) throws IOException {
        packetByteBuf.writeLong(time);
    }
}
