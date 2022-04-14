package me.dustin.chatbot.network.packet.impl.play.s2c;

import me.dustin.chatbot.network.packet.Packet;
import me.dustin.chatbot.network.packet.ProtocolHandler;
import me.dustin.chatbot.network.packet.handler.ClientBoundPacketHandler;
import me.dustin.chatbot.network.packet.handler.PlayClientBoundPacketHandler;
import me.dustin.chatbot.network.packet.pipeline.PacketByteBuf;

import java.util.UUID;

public class ClientBoundSpawnPlayerPacket extends Packet.ClientBoundPacket {
    private final int entityId;
    private final UUID playerUUID;
    private final double x, y, z;
    private final byte yaw, pitch;
    public ClientBoundSpawnPlayerPacket(PacketByteBuf packetByteBuf) {
        super(packetByteBuf);
        this.entityId = packetByteBuf.readVarInt();
        //just discard all data from 1.7, really not needed
        if (ProtocolHandler.getCurrent().getProtocolVer() <= ProtocolHandler.getVersionFromName("1.7.10").getProtocolVer()) {
            this.playerUUID = UUID.fromString(packetByteBuf.readString());
            packetByteBuf.readString();//player name
            int discardedDataCount = packetByteBuf.readVarInt();
            for (int i = 0; i < discardedDataCount; i++) {
                packetByteBuf.readString();
                packetByteBuf.readString();
                packetByteBuf.readString();
            }
        } else {
            this.playerUUID = packetByteBuf.readUuid();
        }
        if (ProtocolHandler.getCurrent().getProtocolVer() <= ProtocolHandler.getVersionFromName("1.8.9").getProtocolVer()) {
            this.x = (packetByteBuf.readInt() / 32.D);
            this.y = (packetByteBuf.readInt() / 32.D);
            this.z = (packetByteBuf.readInt() / 32.D);
        } else {
            this.x = packetByteBuf.readDouble();
            this.y = packetByteBuf.readDouble();
            this.z = packetByteBuf.readDouble();
        }
        this.yaw = packetByteBuf.readByte();
        this.pitch = packetByteBuf.readByte();
    }

    @Override
    public void apply(ClientBoundPacketHandler clientBoundPacketHandler) {
        ((PlayClientBoundPacketHandler)clientBoundPacketHandler).handleSpawnPlayerPacket(this);
    }

    public int getEntityId() {
        return entityId;
    }

    public UUID getPlayerUUID() {
        return playerUUID;
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

    public byte getYaw() {
        return yaw;
    }

    public byte getPitch() {
        return pitch;
    }
}
