package me.dustin.chatbot.network.packet.s2c.play;

import me.dustin.chatbot.network.packet.Packet;
import me.dustin.chatbot.network.packet.handler.ClientBoundPlayClientBoundPacketHandler;
import me.dustin.chatbot.network.packet.handler.ClientBoundPacketHandler;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

public class ClientBoundWorldTimePacket extends Packet.ClientBoundPacket {
    private long worldAge;
    private long timeOfDay;
    public ClientBoundWorldTimePacket(ClientBoundPacketHandler clientBoundPacketHandler) {
        super(clientBoundPacketHandler);
    }

    @Override
    public void createPacket(DataInputStream dataInputStream) throws IOException {
        this.worldAge = dataInputStream.readLong();
        this.timeOfDay = dataInputStream.readLong();
    }

    @Override
    public void apply() {
        ((ClientBoundPlayClientBoundPacketHandler)clientBoundPacketHandler).handleWorldTimePacket(this);
    }

    public long getWorldAge() {
        return worldAge;
    }

    public long getTimeOfDay() {
        return timeOfDay;
    }
}
