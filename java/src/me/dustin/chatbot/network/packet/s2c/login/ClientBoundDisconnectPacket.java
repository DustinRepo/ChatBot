package me.dustin.chatbot.network.packet.s2c.login;

import me.dustin.chatbot.network.packet.pipeline.PacketByteBuf;
import me.dustin.chatbot.network.packet.Packet;
import me.dustin.chatbot.network.packet.handler.ClientBoundLoginClientBoundPacketHandler;
import me.dustin.chatbot.network.packet.handler.ClientBoundPacketHandler;

import java.io.IOException;

public class ClientBoundDisconnectPacket extends Packet.ClientBoundPacket {
    private String reason;

    public ClientBoundDisconnectPacket(ClientBoundPacketHandler clientBoundPacketHandler) {
        super(0x00, clientBoundPacketHandler);
    }

    @Override
    public void createPacket(PacketByteBuf packetByteBuf) throws IOException {
        this.reason = packetByteBuf.readString();
    }

    @Override
    public void apply() {
        ((ClientBoundLoginClientBoundPacketHandler)clientBoundPacketHandler).handleDisconnectPacket(this);
    }

    public String getReason() {
        return reason;
    }
}
