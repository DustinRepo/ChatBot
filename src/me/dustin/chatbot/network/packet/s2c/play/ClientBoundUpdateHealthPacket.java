package me.dustin.chatbot.network.packet.s2c.play;

import me.dustin.chatbot.network.ClientBoundPacketHandler;
import me.dustin.chatbot.network.packet.Packet;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

public class ClientBoundUpdateHealthPacket extends Packet.ClientBoundPacket {

    private float health;
    private int food;
    private float saturation;

    public ClientBoundUpdateHealthPacket(ClientBoundPacketHandler clientBoundPacketHandler) {
        super(0x52, clientBoundPacketHandler);
    }

    @Override
    public void createPacket(ByteArrayInputStream byteArrayInputStream) throws IOException {
        byte[] floatArray = new byte[8];
        DataInputStream inputStream = new DataInputStream(byteArrayInputStream);
        this.health = inputStream.readFloat();
        this.food = readVarInt(inputStream);
        this.saturation = inputStream.readFloat();
        super.createPacket(byteArrayInputStream);
    }

    @Override
    public void apply() {
        clientBoundPacketHandler.handleUpdateHealthPacket(this);
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
