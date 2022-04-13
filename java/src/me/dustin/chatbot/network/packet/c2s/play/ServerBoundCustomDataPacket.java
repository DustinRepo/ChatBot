package me.dustin.chatbot.network.packet.c2s.play;

import me.dustin.chatbot.network.packet.Packet;
import me.dustin.chatbot.network.packet.ProtocolHandler;
import me.dustin.chatbot.network.packet.pipeline.PacketByteBuf;

import java.io.IOException;

public class ServerBoundCustomDataPacket extends Packet {
    private final String channel;
    private final PacketByteBuf data;
    public ServerBoundCustomDataPacket(String channel, PacketByteBuf data) {
        super(ProtocolHandler.getCurrent().getPacketId(ProtocolHandler.NetworkSide.SERVERBOUND, "plugin"));
        this.channel = channel;
        this.data = data;
    }

    @Override
    public void createPacket(PacketByteBuf packetByteBuf) throws IOException {
        packetByteBuf.writeString(channel);
        if (ProtocolHandler.getCurrent().getProtocolVer() <= ProtocolHandler.getVersionFromName("1.7.10").getProtocolVer())
            packetByteBuf.writeShort(data.readableBytes());
        packetByteBuf.writeBytes(data);
        super.createPacket(packetByteBuf);
    }
}
