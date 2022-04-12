package me.dustin.chatbot.network.packet.c2s.play;

import me.dustin.chatbot.network.packet.Packet;
import me.dustin.chatbot.network.packet.ProtocolHandler;
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
        packetByteBuf.writeDouble(z);
        packetByteBuf.writeFloat(yaw);
        packetByteBuf.writeFloat(pitch);
        packetByteBuf.writeBoolean(onGround);
        super.createPacket(packetByteBuf);
    }
}
