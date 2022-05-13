package me.dustin.chatbot.network.packet.impl.play.s2c;

import me.dustin.chatbot.chat.ChatMessage;
import me.dustin.chatbot.network.packet.Packet;
import me.dustin.chatbot.network.packet.handler.ClientBoundPacketHandler;
import me.dustin.chatbot.network.packet.handler.PlayClientBoundPacketHandler;
import me.dustin.chatbot.network.packet.pipeline.PacketByteBuf;

public class ClientBoundChatPreviewPacket extends Packet.ClientBoundPacket {
    private final int id;
    private final String message;
    public ClientBoundChatPreviewPacket(PacketByteBuf packetByteBuf) {
        super(packetByteBuf);
        this.id = packetByteBuf.readInt();
        if (packetByteBuf.readBoolean()) {
            this.message = packetByteBuf.readString();
        } else
            this.message = null;
    }

    @Override
    public void handlePacket(ClientBoundPacketHandler clientBoundPacketHandler) {
        ((PlayClientBoundPacketHandler)clientBoundPacketHandler).handleChatPreviewPacket(this);
    }

    public int getId() {
        return id;
    }

    public String getMessage() {
        return message;
    }
}
