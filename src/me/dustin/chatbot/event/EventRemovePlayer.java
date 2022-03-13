package me.dustin.chatbot.event;

import me.dustin.chatbot.network.player.OtherPlayer;
import me.dustin.events.core.Event;

public class EventRemovePlayer extends Event {

    private OtherPlayer player;

    public EventRemovePlayer(OtherPlayer player) {
        this.player = player;
    }

    public OtherPlayer getPlayer() {
        return player;
    }
}
