package me.dustin.chatbot.network.packet.impl.play.c2s;

import me.dustin.chatbot.network.packet.Packet;
import me.dustin.chatbot.network.packet.ProtocolHandler;
import me.dustin.chatbot.network.packet.pipeline.PacketByteBuf;

import java.io.IOException;

public class ServerBoundTabCompletePacket extends Packet {

    private final int transactionId;
    private final String cmd;

    public ServerBoundTabCompletePacket(int transactionId, String cmd) {
        super(ProtocolHandler.getCurrent().getPacketId(ProtocolHandler.NetworkSide.SERVERBOUND, "chat_suggestions"));
        this.transactionId = transactionId;
        this.cmd = cmd;
    }

    @Override
    public void createPacket(PacketByteBuf packetByteBuf) throws IOException {
        if (ProtocolHandler.getCurrent().getProtocolVer() <= ProtocolHandler.getVersionFromName("1.12.2").getProtocolVer()) {
            packetByteBuf.writeString(cmd);//text
            if (ProtocolHandler.getCurrent().getProtocolVer() > ProtocolHandler.getVersionFromName("1.7.10").getProtocolVer())
            packetByteBuf.writeBoolean(false);//assume command - used for cmd blocks
            if (ProtocolHandler.getCurrent().getProtocolVer() > ProtocolHandler.getVersionFromName("1.8.9").getProtocolVer())
                packetByteBuf.writeBoolean(false);//has position
        } else {
            packetByteBuf.writeVarInt(transactionId);
            packetByteBuf.writeString(cmd);
        }
    }
}
