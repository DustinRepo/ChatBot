package me.dustin.chatbot.network.packet.impl.play.c2s;

import me.dustin.chatbot.network.packet.Packet;
import me.dustin.chatbot.network.ProtocolHandler;
import me.dustin.chatbot.network.packet.pipeline.PacketByteBuf;

import java.io.IOException;

public class ServerBoundPlayerPositionPacket extends Packet {
    private final double x, y, z;
    private final boolean onGround;
    public ServerBoundPlayerPositionPacket(double x, double y, double z, boolean onGround) {
        super(ProtocolHandler.getCurrent().getPacketId(ProtocolHandler.NetworkSide.SERVERBOUND, "position"));
        this.x = x;
        this.y = y;
        this.z = z;
        this.onGround = onGround;
    }

    @Override
    public void createPacket(PacketByteBuf packetByteBuf) throws IOException {
        packetByteBuf.writeDouble(x);
        packetByteBuf.writeDouble(y);
        if (ProtocolHandler.getCurrent().getProtocolVer() <= ProtocolHandler.getVersionFromName("1.7.10").getProtocolVer())
            packetByteBuf.writeDouble(y + 1.62);
        packetByteBuf.writeDouble(z);
        packetByteBuf.writeBoolean(onGround);
        super.createPacket(packetByteBuf);
    }
}
