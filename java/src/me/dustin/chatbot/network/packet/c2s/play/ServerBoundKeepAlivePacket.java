package me.dustin.chatbot.network.packet.c2s.play;

import me.dustin.chatbot.ChatBot;
import me.dustin.chatbot.network.Protocols;
import me.dustin.chatbot.network.packet.Packet;
import me.dustin.chatbot.network.packet.PacketIDs;
import me.dustin.chatbot.network.packet.pipeline.PacketByteBuf;

import java.io.IOException;

public class ServerBoundKeepAlivePacket extends Packet {

    private long id;

    public ServerBoundKeepAlivePacket(long id) {
        super(PacketIDs.ServerBound.KEEP_ALIVE.getPacketId());
        this.id = id;
    }

    @Override
    public void createPacket(PacketByteBuf packetByteBuf) throws IOException {
        if (Protocols.getCurrent().getProtocolVer() <= Protocols.V1_12_1.getProtocolVer())//in 1.12.1 and below keepalive id is an int, not a long
            packetByteBuf.writeVarInt((int) id);
        else
            packetByteBuf.writeLong(id);
    }
}
