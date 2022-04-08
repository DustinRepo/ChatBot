package me.dustin.chatbot.network.packet.s2c.play;

import me.dustin.chatbot.ChatBot;
import me.dustin.chatbot.chat.ChatMessage;
import me.dustin.chatbot.helper.MCAPIHelper;
import me.dustin.chatbot.network.Protocols;
import me.dustin.chatbot.network.packet.Packet;
import me.dustin.chatbot.network.packet.handler.ClientBoundPlayClientBoundPacketHandler;
import me.dustin.chatbot.network.packet.handler.ClientBoundPacketHandler;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.UUID;

public class ClientBoundChatMessagePacket extends Packet.ClientBoundPacket {
    public static int MESSAGE_TYPE_CHAT = 0, MESSAGE_TYPE_SYSTEM = 1, MESSAGE_TYPE_GAME_INFO = 2;
    private ChatMessage message;
    private byte type;
    private UUID sender;

    public ClientBoundChatMessagePacket(ClientBoundPacketHandler clientBoundPacketHandler) {
        super(clientBoundPacketHandler);
    }

    @Override
    public void createPacket(DataInputStream dataInputStream) throws IOException {
        this.message = ChatMessage.of(readString(dataInputStream));
        this.type = dataInputStream.readByte();
        if (ChatBot.getConfig().getProtocolVersion() > Protocols.V1_15_2.getProtocolVer())
            this.sender = readUUID(dataInputStream);
        else if (!this.message.getSenderName().isEmpty()) {
            this.sender = MCAPIHelper.getUUIDFromName(this.message.getSenderName());
        }
        if (sender != null && sender.toString().equalsIgnoreCase("00000000-0000-0000-0000-000000000000"))
            sender = null;
    }

    @Override
    public void apply() {
        ((ClientBoundPlayClientBoundPacketHandler)clientBoundPacketHandler).handleChatMessagePacket(this);
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
