package me.dustin.chatbot.network.packet.s2c.play;

import me.dustin.chatbot.network.packet.pipeline.PacketByteBuf;
import me.dustin.chatbot.network.packet.Packet;
import me.dustin.chatbot.network.packet.handler.PlayClientBoundPacketHandler;
import me.dustin.chatbot.network.packet.handler.ClientBoundPacketHandler;

import java.io.IOException;

public class ClientBoundDisconnectPlayPacket extends Packet.ClientBoundPacket {
    private String reason;

    public ClientBoundDisconnectPlayPacket(ClientBoundPacketHandler clientBoundPacketHandler) {
        super(clientBoundPacketHandler);
    }

    @Override
    public void createPacket(PacketByteBuf packetByteBuf) throws IOException {
        this.reason = packetByteBuf.readString();
    }

    public String getReason() {
        return reason;
    }

    @Override
    public void apply() {
        ((PlayClientBoundPacketHandler)clientBoundPacketHandler).handleDisconnectPacket(this);
    }
}
