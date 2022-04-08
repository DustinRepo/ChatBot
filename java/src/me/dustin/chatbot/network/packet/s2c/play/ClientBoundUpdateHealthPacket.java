package me.dustin.chatbot.network.packet.s2c.play;

import me.dustin.chatbot.network.packet.Packet;
import me.dustin.chatbot.network.packet.handler.ClientBoundPlayClientBoundPacketHandler;
import me.dustin.chatbot.network.packet.handler.ClientBoundPacketHandler;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

public class ClientBoundUpdateHealthPacket extends Packet.ClientBoundPacket {

    private float health;
    private int food;
    private float saturation;

    public ClientBoundUpdateHealthPacket(ClientBoundPacketHandler clientBoundPacketHandler) {
        super(clientBoundPacketHandler);
    }

    @Override
    public void createPacket(DataInputStream dataInputStream) throws IOException {
        this.health = dataInputStream.readFloat();
        this.food = readVarInt(dataInputStream);
        this.saturation = dataInputStream.readFloat();
    }

    @Override
    public void apply() {
        ((ClientBoundPlayClientBoundPacketHandler)clientBoundPacketHandler).handleUpdateHealthPacket(this);
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
