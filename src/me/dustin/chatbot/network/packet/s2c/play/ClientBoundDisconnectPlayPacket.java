package me.dustin.chatbot.network.packet.s2c.play;

import me.dustin.chatbot.network.ClientBoundPacketHandler;
import me.dustin.chatbot.network.packet.Packet;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

public class ClientBoundDisconnectPlayPacket extends Packet.ClientBoundPacket {
    private String reason;

    public ClientBoundDisconnectPlayPacket(ClientBoundPacketHandler clientBoundPacketHandler) {
        super(0x1A, clientBoundPacketHandler);
    }

    @Override
    public void createPacket(ByteArrayInputStream byteArrayInputStream) throws IOException {
        DataInputStream dataInputStream = new DataInputStream(byteArrayInputStream);
        this.reason = readString(dataInputStream);
        super.createPacket(byteArrayInputStream);
    }

    public String getReason() {
        return reason;
    }

    @Override
    public void apply() {
        clientBoundPacketHandler.handleDisconnectPacket(this);
    }
}
