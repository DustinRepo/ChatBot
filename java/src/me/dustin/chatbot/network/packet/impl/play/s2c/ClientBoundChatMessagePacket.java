package me.dustin.chatbot.network.packet.impl.play.s2c;

import me.dustin.chatbot.chat.ChatMessage;
import me.dustin.chatbot.helper.MCAPIHelper;
import me.dustin.chatbot.network.packet.ProtocolHandler;
import me.dustin.chatbot.network.packet.pipeline.PacketByteBuf;
import me.dustin.chatbot.network.packet.Packet;
import me.dustin.chatbot.network.packet.handler.PlayClientBoundPacketHandler;
import me.dustin.chatbot.network.packet.handler.ClientBoundPacketHandler;

import java.util.UUID;

public class ClientBoundChatMessagePacket extends Packet.ClientBoundPacket {
    public final static int MESSAGE_TYPE_CHAT = 0, MESSAGE_TYPE_SYSTEM = 1, MESSAGE_TYPE_GAME_INFO = 2;
    private final ChatMessage message;
    private final byte type;
    private final UUID sender;

    public ClientBoundChatMessagePacket(PacketByteBuf packetByteBuf) {
        super(packetByteBuf);
        UUID uuid = null;
        this.message = ChatMessage.of(packetByteBuf.readString());
        if (ProtocolHandler.getCurrent().getProtocolVer() > ProtocolHandler.getVersionFromName("1.7.10").getProtocolVer())
            this.type = packetByteBuf.readByte();
        else
            this.type = MESSAGE_TYPE_CHAT;
        if (ProtocolHandler.getCurrent().getProtocolVer() > ProtocolHandler.getVersionFromName("1.15.1").getProtocolVer())
            uuid = packetByteBuf.readUuid();
        else if (!this.message.getSenderName().isEmpty()) {
            uuid = MCAPIHelper.getUUIDFromName(this.message.getSenderName());
        }
        if (uuid != null && uuid.toString().equalsIgnoreCase("00000000-0000-0000-0000-000000000000"))
            uuid = null;
        this.sender = uuid;
    }

    @Override
    public void apply(ClientBoundPacketHandler clientBoundPacketHandler) {
        ((PlayClientBoundPacketHandler)clientBoundPacketHandler).handleChatMessagePacket(this);
    }

    public ChatMessage getMessage() {
        return message;
    }

    public byte getType() {
        return type;
    }

    public UUID getSender() {
        return sender;
    }
}
