package me.dustin.chatbot.entity.player;

import java.util.ArrayList;
import java.util.UUID;

public class PlayerInfoManager {

    private final ArrayList<PlayerInfo> players = new ArrayList<>();

    public ArrayList<PlayerInfo> getPlayers() {
        return players;
    }

    public PlayerInfo get(UUID uuid) {
        for (PlayerInfo player : players) {
            if (player.getUuid().toString().equalsIgnoreCase(uuid.toString()))
                return player;
        }
        return null;
    }

    public PlayerInfo get(String name) {
        for (PlayerInfo player : players) {
            if (player.getName().equalsIgnoreCase(name))
                return player;
        }
        return null;
    }
}
