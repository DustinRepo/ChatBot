package me.dustin.chatbot.network.packet.c2s.login;

import me.dustin.chatbot.network.packet.Packet;
import me.dustin.chatbot.network.packet.pipeline.PacketByteBuf;

import java.io.IOException;

public class ServerBoundLoginStartPacket extends Packet {
    private String name;
    public ServerBoundLoginStartPacket(String name) {
        super(0x00);
        this.name = name;
    }

    @Override
    public void createPacket(PacketByteBuf packetByteBuf) throws IOException {
        packetByteBuf.writeString(name);
    }
}
