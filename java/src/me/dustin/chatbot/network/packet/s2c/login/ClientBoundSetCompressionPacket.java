package me.dustin.chatbot.network.packet.s2c.login;

import me.dustin.chatbot.network.packet.pipeline.PacketByteBuf;
import me.dustin.chatbot.network.packet.Packet;
import me.dustin.chatbot.network.packet.handler.LoginClientBoundPacketHandler;
import me.dustin.chatbot.network.packet.handler.ClientBoundPacketHandler;

import java.io.IOException;

public class ClientBoundSetCompressionPacket extends Packet.ClientBoundPacket {
    private int compressionThreshold;
    public ClientBoundSetCompressionPacket(ClientBoundPacketHandler clientBoundPacketHandler) {
        super(0x03, clientBoundPacketHandler);
    }

    @Override
    public void createPacket(PacketByteBuf packetByteBuf) throws IOException {
        this.compressionThreshold = packetByteBuf.readVarInt();
    }

    public int getCompressionThreshold() {
        return compressionThreshold;
    }

    @Override
    public void apply() {
        ((LoginClientBoundPacketHandler)clientBoundPacketHandler).handleCompressionPacket(this);
    }
}
