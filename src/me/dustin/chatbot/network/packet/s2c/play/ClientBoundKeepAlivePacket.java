package me.dustin.chatbot.network.packet.s2c.play;

import me.dustin.chatbot.network.ClientBoundPacketHandler;
import me.dustin.chatbot.network.packet.Packet;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

public class ClientBoundKeepAlivePacket extends Packet.ClientBoundPacket {

    private long id;
    public ClientBoundKeepAlivePacket(ClientBoundPacketHandler clientBoundPacketHandler) {
        super(0x21, clientBoundPacketHandler);
    }

    @Override
    public void createPacket(ByteArrayInputStream byteArrayInputStream) throws IOException {
        DataInputStream dataInputStream = new DataInputStream(byteArrayInputStream);
        this.id = dataInputStream.readLong();
        super.createPacket(byteArrayInputStream);
    }

    public long getId() {
        return id;
    }

    @Override
    public void apply() {
        clientBoundPacketHandler.handleKeepAlive(this);
    }
}
