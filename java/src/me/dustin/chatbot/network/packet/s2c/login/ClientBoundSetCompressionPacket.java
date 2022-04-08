package me.dustin.chatbot.network.packet.s2c.login;

import me.dustin.chatbot.network.packet.Packet;
import me.dustin.chatbot.network.packet.handler.ClientBoundLoginClientBoundPacketHandler;
import me.dustin.chatbot.network.packet.handler.ClientBoundPacketHandler;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

public class ClientBoundSetCompressionPacket extends Packet.ClientBoundPacket {
    private int compressionThreshold;
    public ClientBoundSetCompressionPacket(ClientBoundPacketHandler clientBoundPacketHandler) {
        super(clientBoundPacketHandler);
    }

    @Override
    public void createPacket(DataInputStream dataInputStream) throws IOException {
        this.compressionThreshold = readVarInt(dataInputStream);
    }

    public int getCompressionThreshold() {
        return compressionThreshold;
    }

    @Override
    public void apply() {
        ((ClientBoundLoginClientBoundPacketHandler)clientBoundPacketHandler).handleCompressionPacket(this);
    }
}
