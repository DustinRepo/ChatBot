package me.dustin.chatbot.network.packet.impl.play.c2s;

import me.dustin.chatbot.network.packet.Packet;
import me.dustin.chatbot.network.packet.ProtocolHandler;
import me.dustin.chatbot.network.packet.pipeline.PacketByteBuf;

import java.io.IOException;

public class ServerBoundKeepAlivePacket extends Packet {

    private long id;

    public ServerBoundKeepAlivePacket(long id) {
        super(ProtocolHandler.getCurrent().getPacketId(ProtocolHandler.NetworkSide.SERVERBOUND, "heartbeat"));
        this.id = id;
    }

    @Override
    public void createPacket(PacketByteBuf packetByteBuf) throws IOException {
        if (ProtocolHandler.getCurrent().getProtocolVer() <= ProtocolHandler.getVersionFromName("1.12.1").getProtocolVer())//in 1.12.1 and below keepalive id is an int, not a long
            packetByteBuf.writeVarInt((int) id);
        else
            packetByteBuf.writeLong(id);
    }
}
