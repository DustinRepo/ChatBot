package me.dustin.chatbot.network.packet.c2s.play;

import me.dustin.chatbot.network.packet.Packet;
import me.dustin.chatbot.network.packet.ProtocolHandler;
import me.dustin.chatbot.network.packet.pipeline.PacketByteBuf;

import java.io.IOException;

public class ServerBoundPlayerOnGroundPacket extends Packet {
    private final boolean onGround;
    public ServerBoundPlayerOnGroundPacket(boolean onGround) {
        super(ProtocolHandler.getCurrent().getPacketId(ProtocolHandler.NetworkSide.SERVERBOUND, "ground_change"));
        this.onGround = onGround;
    }

    @Override
    public void createPacket(PacketByteBuf packetByteBuf) throws IOException {
        packetByteBuf.writeBoolean(onGround);
        super.createPacket(packetByteBuf);
    }
}
