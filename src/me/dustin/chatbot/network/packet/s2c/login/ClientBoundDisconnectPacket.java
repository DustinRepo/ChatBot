package me.dustin.chatbot.network.packet.s2c.login;

import me.dustin.chatbot.network.packet.Packet;
import me.dustin.chatbot.network.packet.handler.ClientBoundLoginClientBoundPacketHandler;
import me.dustin.chatbot.network.packet.handler.ClientBoundPacketHandler;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

public class ClientBoundDisconnectPacket extends Packet.ClientBoundPacket {
    private String reason;

    public ClientBoundDisconnectPacket(ClientBoundPacketHandler clientBoundPacketHandler) {
        super(clientBoundPacketHandler);
    }

    @Override
    public void createPacket(ByteArrayInputStream byteArrayInputStream) throws IOException {
        DataInputStream dataInputStream = new DataInputStream(byteArrayInputStream);
        this.reason = readString(dataInputStream);
        super.createPacket(byteArrayInputStream);
    }

    @Override
    public void apply() {
        ((ClientBoundLoginClientBoundPacketHandler)clientBoundPacketHandler).handleDisconnectPacket(this);
    }

    public String getReason() {
        return reason;
    }
}
