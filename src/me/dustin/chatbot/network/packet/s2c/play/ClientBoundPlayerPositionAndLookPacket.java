package me.dustin.chatbot.network.packet.s2c.play;

import me.dustin.chatbot.ChatBot;
import me.dustin.chatbot.network.Protocols;
import me.dustin.chatbot.network.packet.Packet;
import me.dustin.chatbot.network.packet.handler.ClientBoundPacketHandler;
import me.dustin.chatbot.network.packet.handler.ClientBoundPlayClientBoundPacketHandler;

import java.io.DataInputStream;
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
    public void createPacket(DataInputStream dataInputStream) throws IOException {
        x = dataInputStream.readDouble();
        y = dataInputStream.readDouble();
        z = dataInputStream.readDouble();
        yaw = dataInputStream.readFloat();
        pitch = dataInputStream.readFloat();
        flags = dataInputStream.readByte();
        teleportId = readVarInt(dataInputStream);
        if (ChatBot.getConfig().getProtocolVersion() > Protocols.V1_16_5.getProtocolVer())//1.16.5
            dismount = dataInputStream.readBoolean();
    }

    @Override
    public void apply() {
        ((ClientBoundPlayClientBoundPacketHandler)clientBoundPacketHandler).handlePlayerPositionAndLookPacket(this);
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
