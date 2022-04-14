package me.dustin.chatbot.network.packet.s2c.play;

import me.dustin.chatbot.network.packet.pipeline.PacketByteBuf;
import me.dustin.chatbot.network.packet.Packet;
import me.dustin.chatbot.network.packet.handler.PlayClientBoundPacketHandler;
import me.dustin.chatbot.network.packet.handler.ClientBoundPacketHandler;

import java.io.IOException;

public class ClientBoundDisconnectPlayPacket extends Packet.ClientBoundPacket {
    private final String reason;

    public ClientBoundDisconnectPlayPacket(PacketByteBuf packetByteBuf) {
        super(packetByteBuf);
        this.reason = packetByteBuf.readString();
    }

    public String getReason() {
        return reason;
    }

    @Override
    public void apply(ClientBoundPacketHandler clientBoundPacketHandler) {
        ((PlayClientBoundPacketHandler)clientBoundPacketHandler).handleDisconnectPacket(this);
    }
}
