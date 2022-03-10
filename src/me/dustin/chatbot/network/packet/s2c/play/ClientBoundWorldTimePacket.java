package me.dustin.chatbot.network.packet.s2c.play;

import me.dustin.chatbot.network.ClientBoundPacketHandler;
import me.dustin.chatbot.network.packet.Packet;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

public class ClientBoundWorldTimePacket extends Packet.ClientBoundPacket {
    private long worldAge;
    private long timeOfDay;
    public ClientBoundWorldTimePacket(ClientBoundPacketHandler clientBoundPacketHandler) {
        super(0x59, clientBoundPacketHandler);
    }

    @Override
    public void createPacket(ByteArrayInputStream byteArrayInputStream) throws IOException {
        DataInputStream dataInputStream = new DataInputStream(byteArrayInputStream);
        worldAge = dataInputStream.readLong();
        timeOfDay = dataInputStream.readLong();
        super.createPacket(byteArrayInputStream);
    }

    @Override
    public void apply() {
        clientBoundPacketHandler.handleWorldTimePacket(this);
    }

    public long getWorldAge() {
        return worldAge;
    }

    public long getTimeOfDay() {
        return timeOfDay;
    }
}
