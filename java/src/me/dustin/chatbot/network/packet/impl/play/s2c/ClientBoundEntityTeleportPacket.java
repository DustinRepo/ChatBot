package me.dustin.chatbot.network.packet.impl.play.s2c;

import me.dustin.chatbot.network.packet.Packet;
import me.dustin.chatbot.network.ProtocolHandler;
import me.dustin.chatbot.network.packet.handler.ClientBoundPacketHandler;
import me.dustin.chatbot.network.packet.handler.PlayClientBoundPacketHandler;
import me.dustin.chatbot.network.packet.pipeline.PacketByteBuf;

public class ClientBoundEntityTeleportPacket extends Packet.ClientBoundPacket {
    private final int entityId;
    private final double x, y, z;
    private final byte yaw, pitch;
    private final boolean onGround;
    public ClientBoundEntityTeleportPacket(PacketByteBuf packetByteBuf) {
        super(packetByteBuf);
        this.entityId = packetByteBuf.readVarInt();

        if (ProtocolHandler.getCurrent().getProtocolVer() <= ProtocolHandler.getVersionFromName("1.8.9").getProtocolVer()) {
            this.x = packetByteBuf.readInt() / 32.D;
            this.y = packetByteBuf.readInt() / 32.D + 0.015625D;
            this.z = packetByteBuf.readInt() / 32.D;
        } else {
            this.x = packetByteBuf.readDouble();
            this.y = packetByteBuf.readDouble();
            this.z = packetByteBuf.readDouble();
        }
        this.yaw = packetByteBuf.readByte();
        this.pitch = packetByteBuf.readByte();
        if (ProtocolHandler.getCurrent().getProtocolVer() >= ProtocolHandler.getVersionFromName("1.8.9").getProtocolVer())
            this.onGround = packetByteBuf.readBoolean();
        else
            this.onGround = true;
    }

    @Override
    public void handlePacket(ClientBoundPacketHandler clientBoundPacketHandler) {
        ((PlayClientBoundPacketHandler)clientBoundPacketHandler).handleEntityTeleportPacket(this);
    }

    public int getEntityId() {
        return entityId;
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

    public boolean isOnGround() {
        return onGround;
    }
}
