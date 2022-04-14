package me.dustin.chatbot.network.packet.impl.play.s2c;

import me.dustin.chatbot.network.packet.Packet;
import me.dustin.chatbot.network.packet.ProtocolHandler;
import me.dustin.chatbot.network.packet.handler.ClientBoundPacketHandler;
import me.dustin.chatbot.network.packet.handler.PlayClientBoundPacketHandler;
import me.dustin.chatbot.network.packet.pipeline.PacketByteBuf;

public class ClientBoundEntityPositionAndRotationPacket extends Packet.ClientBoundPacket {
    private final int entityId;
    private final short deltaX;
    private final short deltaY;
    private final short deltaZ;
    private final double oldDeltaX;
    private final double oldDeltaY;
    private final double oldDeltaZ;
    private final byte yaw;
    private final byte pitch;
    private final boolean onGround;
    public ClientBoundEntityPositionAndRotationPacket(PacketByteBuf packetByteBuf) {
        super(packetByteBuf);
        this.entityId = packetByteBuf.readVarInt();
        if (ProtocolHandler.getCurrent().getProtocolVer() <= ProtocolHandler.getVersionFromName("1.8.9").getProtocolVer()) {
            this.oldDeltaX = packetByteBuf.readByte() / 32.D;
            this.oldDeltaY = packetByteBuf.readByte() / 32.D;
            this.oldDeltaZ = packetByteBuf.readByte() / 32.D;
            this.deltaX = -9999;
            this.deltaY = -9999;
            this.deltaZ = -9999;
            this.yaw = packetByteBuf.readByte();
            this.pitch = packetByteBuf.readByte();
            this.onGround = true;
            return;
        }

        this.deltaX = packetByteBuf.readShort();
        this.deltaY = packetByteBuf.readShort();
        this.deltaZ = packetByteBuf.readShort();
        this.oldDeltaX = -9999;
        this.oldDeltaY = -9999;
        this.oldDeltaZ = -9999;
        this.yaw = packetByteBuf.readByte();
        this.pitch = packetByteBuf.readByte();
        this.onGround = packetByteBuf.readBoolean();
    }

    @Override
    public void apply(ClientBoundPacketHandler clientBoundPacketHandler) {
        ((PlayClientBoundPacketHandler)clientBoundPacketHandler).handleEntityPositionAndRotationPacket(this);
    }

    public static long encodePacketCoordinate(double coord) {
        return lfloor(coord * 4096.0);
    }

    public static double decodePacketCoordinate(long coord) {
        return (double)coord / 4096.0;
    }

    private static long lfloor(double value) {
        long l = (long)value;
        return value < (double)l ? l - 1L : l;
    }

    public int getEntityId() {
        return entityId;
    }

    public short getDeltaX() {
        return deltaX;
    }

    public short getDeltaY() {
        return deltaY;
    }

    public short getDeltaZ() {
        return deltaZ;
    }

    public double getOldDeltaX() {
        return oldDeltaX;
    }

    public double getOldDeltaY() {
        return oldDeltaY;
    }

    public double getOldDeltaZ() {
        return oldDeltaZ;
    }

    public boolean isOnGround() {
        return onGround;
    }

    public byte getYaw() {
        return yaw;
    }

    public byte getPitch() {
        return pitch;
    }
}
