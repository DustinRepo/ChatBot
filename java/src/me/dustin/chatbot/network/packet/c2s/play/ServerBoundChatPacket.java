package me.dustin.chatbot.network.packet.c2s.play;

import me.dustin.chatbot.network.packet.Packet;
import me.dustin.chatbot.network.packet.PacketIDs;
import me.dustin.chatbot.network.packet.pipeline.PacketByteBuf;

import java.io.IOException;

public class ServerBoundChatPacket extends Packet {
    String message;
    public ServerBoundChatPacket(String message) {
        super(PacketIDs.ServerBound.CHAT_MESSAGE.getPacketId());
        if (message.length() > 256) {
            message = message.substring(0, 256);
        }
        this.message = message;
    }

    @Override
    public void createPacket(PacketByteBuf packetByteBuf) throws IOException {
        packetByteBuf.writeString(message);
    }
}
