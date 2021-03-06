package me.dustin.chatbot.network.packet.impl.login.s2c;

import me.dustin.chatbot.network.packet.pipeline.PacketByteBuf;
import me.dustin.chatbot.network.packet.Packet;
import me.dustin.chatbot.network.packet.handler.LoginClientBoundPacketHandler;
import me.dustin.chatbot.network.packet.handler.ClientBoundPacketHandler;

public class ClientBoundDisconnectLoginPacket extends Packet.ClientBoundPacket {
    private final String reason;

    public ClientBoundDisconnectLoginPacket(PacketByteBuf packetByteBuf) {
        super(packetByteBuf);
        this.reason = packetByteBuf.readString();
    }

    @Override
    public void handlePacket(ClientBoundPacketHandler clientBoundPacketHandler) {
        ((LoginClientBoundPacketHandler)clientBoundPacketHandler).handleDisconnectPacket(this);
    }

    public String getReason() {
        return reason;
    }
}
