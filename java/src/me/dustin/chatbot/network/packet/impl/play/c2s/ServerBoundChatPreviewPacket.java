package me.dustin.chatbot.network.packet.impl.play.c2s;

import me.dustin.chatbot.network.ProtocolHandler;
import me.dustin.chatbot.network.packet.Packet;
import me.dustin.chatbot.network.packet.pipeline.PacketByteBuf;

import java.io.IOException;

public class ServerBoundChatPreviewPacket extends Packet {
    private final int id;
    private final String message;
    public ServerBoundChatPreviewPacket(int id, String message) {
        super(ProtocolHandler.getCurrent().getPacketId(ProtocolHandler.NetworkSide.SERVERBOUND, "chat_preview"));
        this.id = id;
        if (message.length() > 256) {
            message = message.substring(0, 256);
        }
        this.message = message;
    }

    @Override
    public void createPacket(PacketByteBuf packetByteBuf) throws IOException {
        packetByteBuf.writeInt(this.id);
        packetByteBuf.writeString(message, 256);
    }
}
