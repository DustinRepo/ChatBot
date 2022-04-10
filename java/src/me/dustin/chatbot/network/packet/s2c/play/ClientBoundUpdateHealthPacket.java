package me.dustin.chatbot.network.packet.s2c.play;

import me.dustin.chatbot.network.packet.PacketIDs;
import me.dustin.chatbot.network.packet.pipeline.PacketByteBuf;
import me.dustin.chatbot.network.packet.Packet;
import me.dustin.chatbot.network.packet.handler.PlayClientBoundPacketHandler;
import me.dustin.chatbot.network.packet.handler.ClientBoundPacketHandler;

import java.io.IOException;

public class ClientBoundUpdateHealthPacket extends Packet.ClientBoundPacket {

    private float health;
    private int food;
    private float saturation;

    public ClientBoundUpdateHealthPacket(ClientBoundPacketHandler clientBoundPacketHandler) {
        super(PacketIDs.ClientBound.UPDATE_HEALTH.getPacketId(), clientBoundPacketHandler);
    }

    @Override
    public void createPacket(PacketByteBuf packetByteBuf) throws IOException {
        this.health = packetByteBuf.readFloat();
        this.food = packetByteBuf.readVarInt();
        this.saturation = packetByteBuf.readFloat();
    }

    @Override
    public void apply() {
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
