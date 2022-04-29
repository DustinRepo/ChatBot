package me.dustin.chatbot.network.packet.impl.play.s2c;

import me.dustin.chatbot.chat.ChatMessage;
import me.dustin.chatbot.network.packet.Packet;
import me.dustin.chatbot.network.packet.handler.ClientBoundPacketHandler;
import me.dustin.chatbot.network.packet.handler.PlayClientBoundPacketHandler;
import me.dustin.chatbot.network.packet.pipeline.PacketByteBuf;

public class ClientBoundGameMessagePacket extends Packet.ClientBoundPacket {
    private final ChatMessage message;
    private final int type;
    public ClientBoundGameMessagePacket(PacketByteBuf packetByteBuf) {
        super(packetByteBuf);
        this.message = ChatMessage.of(packetByteBuf.readString());
        this.type = packetByteBuf.readByte();
    }

    @Override
    public void handlePacket(ClientBoundPacketHandler clientBoundPacketHandler) {
        ((PlayClientBoundPacketHandler)clientBoundPacketHandler).handleGameMessagePacket(this);
    }

    public ChatMessage getMessage() {
        return message;
    }

    public int getType() {
        return type;
    }
}
