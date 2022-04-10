package me.dustin.chatbot.network.packet.s2c.login;

import me.dustin.chatbot.network.packet.pipeline.PacketByteBuf;
import me.dustin.chatbot.network.packet.Packet;
import me.dustin.chatbot.network.packet.handler.LoginClientBoundPacketHandler;
import me.dustin.chatbot.network.packet.handler.ClientBoundPacketHandler;

import java.io.IOException;

public class ClientBoundDisconnectLoginPacket extends Packet.ClientBoundPacket {
    private String reason;

    public ClientBoundDisconnectLoginPacket(ClientBoundPacketHandler clientBoundPacketHandler) {
        super(clientBoundPacketHandler);
    }

    @Override
    public void createPacket(PacketByteBuf packetByteBuf) throws IOException {
        this.reason = packetByteBuf.readString();
    }

    @Override
    public void apply() {
        ((LoginClientBoundPacketHandler)clientBoundPacketHandler).handleDisconnectPacket(this);
    }

    public String getReason() {
        return reason;
    }
}
