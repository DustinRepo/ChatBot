package me.dustin.chatbot.network.packet.impl.play.s2c;

import me.dustin.chatbot.network.packet.Packet;
import me.dustin.chatbot.network.packet.handler.ClientBoundPacketHandler;
import me.dustin.chatbot.network.packet.handler.PlayClientBoundPacketHandler;
import me.dustin.chatbot.network.packet.pipeline.PacketByteBuf;

public class ClientBoundEntityVelocityPacket extends Packet.ClientBoundPacket {
    private final int entityId;
    private final short veloX;
    private final short veloY;
    private final short veloZ;
    public ClientBoundEntityVelocityPacket(PacketByteBuf packetByteBuf) {
        super(packetByteBuf);
        this.entityId = packetByteBuf.readVarInt();
        this.veloX = packetByteBuf.readShort();
        this.veloY = packetByteBuf.readShort();
        this.veloZ = packetByteBuf.readShort();
    }

    @Override
    public void apply(ClientBoundPacketHandler clientBoundPacketHandler) {
        ((PlayClientBoundPacketHandler)clientBoundPacketHandler).handleEntityVelocityPacket(this);
    }

    public int getEntityId() {
        return entityId;
    }

    public short getVeloX() {
        return veloX;
    }

    public short getVeloY() {
        return veloY;
    }

    public short getVeloZ() {
        return veloZ;
    }
}
