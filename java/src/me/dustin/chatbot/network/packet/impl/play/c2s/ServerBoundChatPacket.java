package me.dustin.chatbot.network.packet.impl.play.c2s;

import me.dustin.chatbot.network.packet.Packet;
import me.dustin.chatbot.network.ProtocolHandler;
import me.dustin.chatbot.network.packet.pipeline.PacketByteBuf;

import java.io.IOException;

public class ServerBoundChatPacket extends Packet {
    String message;
    public ServerBoundChatPacket(String message) {
        super(ProtocolHandler.getCurrent().getPacketId(ProtocolHandler.NetworkSide.SERVERBOUND, "chat_message"));
        if (ProtocolHandler.getCurrent().getProtocolVer() >= ProtocolHandler.getVersionFromName("1.11").getProtocolVer()) {
            if (message.length() > 256) {
                message = message.substring(0, 256);
            }
        } else {
            if (message.length() > 100) {
                message = message.substring(0, 100);
            }
        }
        this.message = message;
    }

    @Override
    public void createPacket(PacketByteBuf packetByteBuf) throws IOException {
        packetByteBuf.writeString(message);
    }
}
