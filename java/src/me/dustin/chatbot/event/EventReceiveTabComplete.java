package me.dustin.chatbot.event;

import me.dustin.chatbot.network.packet.s2c.play.ClientBoundTabCompletePacket;
import me.dustin.events.core.Event;

public class EventReceiveTabComplete extends Event {

    private final ClientBoundTabCompletePacket packet;

    public EventReceiveTabComplete(ClientBoundTabCompletePacket packet) {
        this.packet = packet;
    }

    public ClientBoundTabCompletePacket getPacket() {
        return packet;
    }
}
