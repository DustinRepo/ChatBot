package me.dustin.chatbot.network.packet.s2c.play;

import me.dustin.chatbot.network.packet.ProtocolHandler;
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
        super(clientBoundPacketHandler);
    }

    @Override
    public void createPacket(PacketByteBuf packetByteBuf) throws IOException {
        x = packetByteBuf.readDouble();
        y = packetByteBuf.readDouble();
        z = packetByteBuf.readDouble();
        yaw = packetByteBuf.readFloat();
        pitch = packetByteBuf.readFloat();
        flags = packetByteBuf.readByte();
        if (ProtocolHandler.getCurrent().getProtocolVer() > ProtocolHandler.getVersionFromName("1.18.1").getProtocolVer())
            teleportId = packetByteBuf.readVarInt();
        if (ProtocolHandler.getCurrent().getProtocolVer() > ProtocolHandler.getVersionFromName("1.16.5").getProtocolVer())//1.16.5
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
