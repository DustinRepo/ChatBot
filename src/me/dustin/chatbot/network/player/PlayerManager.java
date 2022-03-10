package me.dustin.chatbot.network.player;

import java.util.ArrayList;
import java.util.UUID;

public class PlayerManager {

    private final ArrayList<OtherPlayer> players = new ArrayList<>();

    public ArrayList<OtherPlayer> getPlayers() {
        return players;
    }

    public OtherPlayer get(UUID uuid) {
        for (OtherPlayer player : players) {
            if (player.getUuid().toString().equalsIgnoreCase(uuid.toString()))
                return player;
        }
        return null;
    }

    public OtherPlayer get(String name) {
        for (OtherPlayer player : players) {
            if (player.getName().equalsIgnoreCase(name))
                return player;
        }
        return null;
    }
}
