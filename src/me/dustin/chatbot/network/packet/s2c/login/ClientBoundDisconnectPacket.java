package me.dustin.chatbot.network.packet.s2c.login;

import me.dustin.chatbot.network.ClientBoundPacketHandler;
import me.dustin.chatbot.network.packet.Packet;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

public class ClientBoundDisconnectPacket extends Packet.ClientBoundPacket {
    private String reason;

    public ClientBoundDisconnectPacket(ClientBoundPacketHandler clientBoundPacketHandler) {
        super(0x00, clientBoundPacketHandler);
    }

    @Override
    public void createPacket(ByteArrayInputStream byteArrayInputStream) throws IOException {
        DataInputStream dataInputStream = new DataInputStream(byteArrayInputStream);
        this.reason = readString(dataInputStream);
        super.createPacket(byteArrayInputStream);
    }

    @Override
    public void apply() {
        clientBoundPacketHandler.handleDisconnectPacket(this);
    }

    public String getReason() {
        return reason;
    }
}
