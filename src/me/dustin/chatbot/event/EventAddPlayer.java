package me.dustin.chatbot.event;

import me.dustin.chatbot.network.player.OtherPlayer;
import me.dustin.events.core.Event;

public class EventAddPlayer extends Event {

    private OtherPlayer player;

    public EventAddPlayer(OtherPlayer player) {
        this.player = player;
    }

    public OtherPlayer getPlayer() {
        return player;
    }
}
