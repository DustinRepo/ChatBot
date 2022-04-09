package me.dustin.chatbot.network.packet.c2s.play;

import me.dustin.chatbot.network.packet.Packet;
import me.dustin.chatbot.network.packet.PacketIDs;
import me.dustin.chatbot.network.packet.pipeline.PacketByteBuf;

import java.io.IOException;

public class ServerBoundPlayerRotationPacket extends Packet {

    private final float yaw, pitch;
    private final boolean onGround;

    public ServerBoundPlayerRotationPacket(float yaw, float pitch, boolean onGround) {
        super(PacketIDs.ServerBound.PLAYER_ROTATION.getPacketId());
        this.yaw = yaw;
        this.pitch = pitch;
        this.onGround = onGround;
    }

    @Override
    public void createPacket(PacketByteBuf packetByteBuf) throws IOException {
        packetByteBuf.writeFloat(yaw);
        packetByteBuf.writeFloat(pitch);
        packetByteBuf.writeBoolean(onGround);
    }
}