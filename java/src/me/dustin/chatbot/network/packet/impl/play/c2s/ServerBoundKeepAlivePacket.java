package me.dustin.chatbot.network.packet.impl.play.c2s;

import me.dustin.chatbot.network.packet.Packet;
import me.dustin.chatbot.network.ProtocolHandler;
import me.dustin.chatbot.network.packet.pipeline.PacketByteBuf;

import java.io.IOException;

public class ServerBoundKeepAlivePacket extends Packet {

    private final long id;

    public ServerBoundKeepAlivePacket(long id) {
        super(ProtocolHandler.getCurrent().getPacketId(ProtocolHandler.NetworkSide.SERVERBOUND, "heartbeat"));
        this.id = id;
    }

    @Override
    public void createPacket(PacketByteBuf packetByteBuf) throws IOException {
        packetByteBuf.writeEitherOr(ProtocolHandler.getCurrent().getProtocolVer() <= ProtocolHandler.getVersionFromName("1.12.1").getProtocolVer(), packetByteBuf1 ->
            packetByteBuf1.writeVarInt((int) id)
        ,packetByteBuf1 ->
            packetByteBuf1.writeLong(id)
        );
    }
}
