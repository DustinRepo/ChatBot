package me.dustin.chatbot.event;

import me.dustin.chatbot.entity.player.PlayerInfo;
import me.dustin.events.core.Event;

public class EventAddPlayer extends Event {

    private final PlayerInfo player;

    public EventAddPlayer(PlayerInfo player) {
        this.player = player;
    }

    public PlayerInfo getPlayer() {
        return player;
    }
}
