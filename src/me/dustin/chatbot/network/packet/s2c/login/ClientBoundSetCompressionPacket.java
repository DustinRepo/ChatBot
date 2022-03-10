package me.dustin.chatbot.network.packet.s2c.login;

import me.dustin.chatbot.network.ClientBoundPacketHandler;
import me.dustin.chatbot.network.packet.Packet;

import java.io.ByteArrayInputStream;
import java.io.IOException;

public class ClientBoundSetCompressionPacket extends Packet.ClientBoundPacket {
    private int compressionThreshold;
    public ClientBoundSetCompressionPacket(ClientBoundPacketHandler clientBoundPacketHandler) {
        super(0x03, clientBoundPacketHandler);
    }

    @Override
    public void createPacket(ByteArrayInputStream dataInputStream) throws IOException {
        compressionThreshold = readVarInt(dataInputStream);
        super.createPacket(dataInputStream);
    }

    public int getCompressionThreshold() {
        return compressionThreshold;
    }

    @Override
    public void apply() {
        clientBoundPacketHandler.handleCompressionPacket(this);
    }
}
