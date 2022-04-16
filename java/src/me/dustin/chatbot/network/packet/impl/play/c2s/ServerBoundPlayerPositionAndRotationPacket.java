package me.dustin.chatbot.network.packet.impl.play.c2s;

import me.dustin.chatbot.network.packet.Packet;
import me.dustin.chatbot.network.ProtocolHandler;
import me.dustin.chatbot.network.packet.pipeline.PacketByteBuf;

import java.io.IOException;

public class ServerBoundPlayerPositionAndRotationPacket extends Packet {
    private final double x, y, z;
    private final float yaw, pitch;
    private final boolean onGround;
    public ServerBoundPlayerPositionAndRotationPacket(double x, double y, double z, float yaw, float pitch, boolean onGround) {
        super(ProtocolHandler.getCurrent().getPacketId(ProtocolHandler.NetworkSide.SERVERBOUND, "position_rotation"));
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
        this.onGround = onGround;
    }

    @Override
    public void createPacket(PacketByteBuf packetByteBuf) throws IOException {
        packetByteBuf.writeDouble(x);
        packetByteBuf.writeDouble(y);
        if (ProtocolHandler.getCurrent().getProtocolVer() <= ProtocolHandler.getVersionFromName("1.7.10").getProtocolVer())
            packetByteBuf.writeDouble(y + 1.62);
        packetByteBuf.writeDouble(z);
        packetByteBuf.writeFloat(yaw);
        packetByteBuf.writeFloat(pitch);
        packetByteBuf.writeBoolean(onGround);
        super.createPacket(packetByteBuf);
    }
}
