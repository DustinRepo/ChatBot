package me.dustin.chatbot.network.packet.impl.play.s2c;

import me.dustin.chatbot.network.packet.Packet;
import me.dustin.chatbot.network.packet.ProtocolHandler;
import me.dustin.chatbot.network.packet.handler.ClientBoundPacketHandler;
import me.dustin.chatbot.network.packet.handler.PlayClientBoundPacketHandler;
import me.dustin.chatbot.network.packet.pipeline.PacketByteBuf;

import java.util.UUID;

public class ClientBoundSpawnEntityPacket extends Packet.ClientBoundPacket {
    private final int entityId;
    private final UUID uuid;
    private final int type;
    private final double x, y, z;
    private final byte yaw, pitch;
    private final int data;
    private final short velocityX, velocityY, velocityZ;

    public ClientBoundSpawnEntityPacket(PacketByteBuf packetByteBuf) {
        super(packetByteBuf);
        this.entityId = packetByteBuf.readVarInt();
        if (ProtocolHandler.getCurrent().getProtocolVer() >= ProtocolHandler.getVersionFromName("1.9.1-pre1").getProtocolVer())
            this.uuid = packetByteBuf.readUuid();
        else
            this.uuid = UUID.randomUUID();
        this.type = ProtocolHandler.getCurrent().getProtocolVer() <= ProtocolHandler.getVersionFromName("1.13.2").getProtocolVer() ? packetByteBuf.readByte() : packetByteBuf.readVarInt();
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
        this.data = ProtocolHandler.getCurrent().getProtocolVer() > ProtocolHandler.getVersionFromName("1.18.2").getProtocolVer() ? packetByteBuf.readVarInt() : packetByteBuf.readInt();
        if (ProtocolHandler.getCurrent().getProtocolVer() <= ProtocolHandler.getVersionFromName("1.8.9").getProtocolVer()) {
            if (data != 0) {
                this.velocityX = packetByteBuf.readShort();
                this.velocityY = packetByteBuf.readShort();
                this.velocityZ = packetByteBuf.readShort();
            } else {
                this.velocityX = 0;
                this.velocityY = 0;
                this.velocityZ = 0;
            }
        } else {
            this.velocityX = packetByteBuf.readShort();
            this.velocityY = packetByteBuf.readShort();
            this.velocityZ = packetByteBuf.readShort();
        }
    }

    @Override
    public void apply(ClientBoundPacketHandler clientBoundPacketHandler) {
        //pre 1.13 minecraft hardcoded the entity type value for non-living entities into the packet handling, and I am not going through all that bs
        //I don't even use non-living entities anyway so it's not a big deal
        //this class only exists because in 1.19 snapshots they merged the SpawnMob packet into this
        if (ProtocolHandler.getCurrent().getProtocolVer() <= ProtocolHandler.getVersionFromName("1.12.2").getProtocolVer())
            return;
        ((PlayClientBoundPacketHandler)clientBoundPacketHandler).handleSpawnEntityPacket(this);
    }

    public int getEntityId() {
        return entityId;
    }

    public UUID getUuid() {
        return uuid;
    }

    public int getType() {
        return type;
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

    public int getData() {
        return data;
    }

    public short getVelocityX() {
        return velocityX;
    }

    public short getVelocityY() {
        return velocityY;
    }

    public short getVelocityZ() {
        return velocityZ;
    }
}
