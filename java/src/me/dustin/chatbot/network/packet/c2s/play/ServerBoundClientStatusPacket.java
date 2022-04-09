package me.dustin.chatbot.network.packet.c2s.play;

import me.dustin.chatbot.network.packet.Packet;
import me.dustin.chatbot.network.packet.PacketIDs;
import me.dustin.chatbot.network.packet.pipeline.PacketByteBuf;

import java.io.IOException;

public class ServerBoundClientStatusPacket extends Packet {
    public static final int RESPAWN = 0, REQUEST_STATS = 1;
    private final int action;
    public ServerBoundClientStatusPacket(int action) {
        super(PacketIDs.ServerBound.CLIENT_STATUS.getPacketId());
        this.action = action;
    }

    @Override
    public void createPacket(PacketByteBuf packetByteBuf) throws IOException {
        packetByteBuf.writeVarInt(action);
    }
}
