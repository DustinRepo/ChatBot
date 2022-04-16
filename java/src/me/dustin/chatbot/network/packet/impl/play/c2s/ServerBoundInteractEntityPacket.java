package me.dustin.chatbot.network.packet.impl.play.c2s;

import me.dustin.chatbot.entity.LivingEntity;
import me.dustin.chatbot.network.packet.Packet;
import me.dustin.chatbot.network.packet.ProtocolHandler;
import me.dustin.chatbot.network.packet.pipeline.PacketByteBuf;

import java.io.IOException;

public class ServerBoundInteractEntityPacket extends Packet {
    public static final int INTERACT = 0, ATTACK = 1, INTERACT_AT = 2;
    private final LivingEntity livingEntity;
    private final int useType;
    public ServerBoundInteractEntityPacket(LivingEntity livingEntity, int useType) {
        super(ProtocolHandler.getCurrent().getPacketId(ProtocolHandler.NetworkSide.SERVERBOUND, "entity_interact"));
        this.livingEntity = livingEntity;
        this.useType = useType;
    }

    @Override
    public void createPacket(PacketByteBuf packetByteBuf) throws IOException {
        if (ProtocolHandler.getCurrent().getProtocolVer() <= ProtocolHandler.getVersionFromName("1.7.10").getProtocolVer() && useType == INTERACT_AT)
            return;
        packetByteBuf.writeVarInt(livingEntity.getEntityId());
        packetByteBuf.writeVarInt(useType);
        if (useType == INTERACT_AT) {
            packetByteBuf.writeFloat((float)livingEntity.getX());
            packetByteBuf.writeFloat((float)livingEntity.getY());
            packetByteBuf.writeFloat((float)livingEntity.getZ());
            if (ProtocolHandler.getCurrent().getProtocolVer() >= ProtocolHandler.getVersionFromName("1.9.1-pre1").getProtocolVer())
                packetByteBuf.writeVarInt(0);//hand - 0 is main
        }
        if (ProtocolHandler.getCurrent().getProtocolVer() > ProtocolHandler.getVersionFromName("1.15.2").getProtocolVer())
            packetByteBuf.writeBoolean(false);//sneaking
        super.createPacket(packetByteBuf);
    }
}
