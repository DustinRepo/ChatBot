package me.dustin.chatbot.network.packet.c2s.play;

import me.dustin.chatbot.network.Protocols;
import me.dustin.chatbot.network.packet.Packet;
import me.dustin.chatbot.network.packet.PacketIDs;
import me.dustin.chatbot.network.packet.pipeline.PacketByteBuf;

import java.io.IOException;

public class ServerBoundTabCompletePacket extends Packet {

    private final int transactionId;
    private final String cmd;

    public ServerBoundTabCompletePacket(int transactionId, String cmd) {
        super(PacketIDs.ServerBound.TAB_COMPLETE.getPacketId());
        this.transactionId = transactionId;
        this.cmd = cmd;
    }

    @Override
    public void createPacket(PacketByteBuf packetByteBuf) throws IOException {
        if (Protocols.getCurrent().getProtocolVer() <= Protocols.V1_12_2.getProtocolVer()) {
            packetByteBuf.writeString(cmd);//text
            packetByteBuf.writeBoolean(false);//assume command - used for cmd blocks
            packetByteBuf.writeBoolean(false);//has position
        } else {
            packetByteBuf.writeVarInt(transactionId);
            packetByteBuf.writeString(cmd);
        }
    }
}
