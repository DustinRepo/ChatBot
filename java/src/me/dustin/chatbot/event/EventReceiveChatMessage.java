package me.dustin.chatbot.event;

import me.dustin.chatbot.chat.ChatMessage;
import me.dustin.chatbot.network.packet.s2c.play.ClientBoundChatMessagePacket;
import me.dustin.events.core.Event;

public class EventReceiveChatMessage extends Event {

    private final ClientBoundChatMessagePacket chatMessagePacket;

    public EventReceiveChatMessage(ClientBoundChatMessagePacket chatMessagePacket) {
        this.chatMessagePacket = chatMessagePacket;
    }

    public ClientBoundChatMessagePacket getChatMessagePacket() {
        return chatMessagePacket;
    }
}
