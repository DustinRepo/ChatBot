package me.dustin.chatbot.network.packet.s2c.play;

import me.dustin.chatbot.network.Protocols;
import me.dustin.chatbot.network.packet.PacketIDs;
import me.dustin.chatbot.network.packet.pipeline.PacketByteBuf;
import me.dustin.chatbot.network.packet.Packet;
import me.dustin.chatbot.network.packet.handler.ClientBoundPacketHandler;
import me.dustin.chatbot.network.packet.handler.PlayClientBoundPacketHandler;

import java.io.IOException;

public class ClientBoundPlayerPositionAndLookPacket extends Packet.ClientBoundPacket {
    private double x, y, z;
    private float yaw, pitch;
    private byte flags;
    private int teleportId;
    private boolean dismount;

    public ClientBoundPlayerPositionAndLookPacket(ClientBoundPacketHandler clientBoundPacketHandler) {
        super(PacketIDs.ClientBound.PLAYER_POS_LOOK.getPacketId(), clientBoundPacketHandler);
    }

    @Override
    public void createPacket(PacketByteBuf packetByteBuf) throws IOException {
        x = packetByteBuf.readDouble();
        y = packetByteBuf.readDouble();
        z = packetByteBuf.readDouble();
        yaw = packetByteBuf.readFloat();
        pitch = packetByteBuf.readFloat();
        flags = packetByteBuf.readByte();
        if (Protocols.getCurrent().getProtocolVer() > Protocols.V1_18.getProtocolVer())
            teleportId = packetByteBuf.readVarInt();
        if (Protocols.getCurrent().getProtocolVer() > Protocols.V1_16_5.getProtocolVer())//1.16.5
            dismount = packetByteBuf.readBoolean();
    }

    @Override
    public void apply() {
        ((PlayClientBoundPacketHandler)clientBoundPacketHandler).handlePlayerPositionAndLookPacket(this);
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    public float getYaw() {
        return yaw;
    }

    public float getPitch() {
        return pitch;
    }

    public byte getFlags() {
        return flags;
    }

    public int getTeleportId() {
        return teleportId;
    }

    public boolean isDismount() {
        return dismount;
    }
}
