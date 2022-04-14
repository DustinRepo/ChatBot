package me.dustin.chatbot.event;

import me.dustin.chatbot.network.packet.impl.play.s2c.ClientBoundChatMessagePacket;
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
