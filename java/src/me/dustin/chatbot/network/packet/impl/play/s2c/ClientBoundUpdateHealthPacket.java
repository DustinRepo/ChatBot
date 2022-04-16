package me.dustin.chatbot.network.packet.impl.play.s2c;

import me.dustin.chatbot.network.ProtocolHandler;
import me.dustin.chatbot.network.packet.pipeline.PacketByteBuf;
import me.dustin.chatbot.network.packet.Packet;
import me.dustin.chatbot.network.packet.handler.PlayClientBoundPacketHandler;
import me.dustin.chatbot.network.packet.handler.ClientBoundPacketHandler;

public class ClientBoundUpdateHealthPacket extends Packet.ClientBoundPacket {

    private final float health;
    private final int food;
    private final float saturation;

    public ClientBoundUpdateHealthPacket(PacketByteBuf packetByteBuf) {
        super(packetByteBuf);
        this.health = packetByteBuf.readFloat();
        if (ProtocolHandler.getCurrent().getProtocolVer() <= ProtocolHandler.getVersionFromName("1.7.10").getProtocolVer())
            this.food = packetByteBuf.readShort();
        else
            this.food = packetByteBuf.readVarInt();
        this.saturation = packetByteBuf.readFloat();
    }

    @Override
    public void handlePacket(ClientBoundPacketHandler clientBoundPacketHandler) {
        ((PlayClientBoundPacketHandler)clientBoundPacketHandler).handleUpdateHealthPacket(this);
    }

    public float getHealth() {
        return health;
    }

    public int getFood() {
        return food;
    }

    public float getSaturation() {
        return saturation;
    }
}
