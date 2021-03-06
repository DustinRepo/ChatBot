package me.dustin.chatbot.network.packet.impl.play.c2s;

import me.dustin.chatbot.network.packet.Packet;
import me.dustin.chatbot.network.ProtocolHandler;
import me.dustin.chatbot.network.packet.pipeline.PacketByteBuf;

import java.io.IOException;

public class ServerBoundConfirmTeleportPacket extends Packet {

    private final int id;

    public ServerBoundConfirmTeleportPacket(int id) {
        super(ProtocolHandler.getCurrent().getPacketId(ProtocolHandler.NetworkSide.SERVERBOUND, "confirm_teleport"));
        this.id = id;
    }

    @Override
    public void createPacket(PacketByteBuf packetByteBuf) throws IOException {
        packetByteBuf.writeVarInt(id);
    }
}
