package me.dustin.chatbot.network.player;

import java.util.ArrayList;
import java.util.UUID;

public class OtherPlayer {

    private String name;
    private UUID uuid;
    private GameMode gameMode;
    private int ping;
    private String displayName;

    public final ArrayList<PlayerProperty> properties = new ArrayList<>();

    public OtherPlayer(String name, UUID uuid) {
        this.name = name;
        this.uuid = uuid;
        displayName = "";
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public UUID getUuid() {
        return uuid;
    }

    public GameMode getGameMode() {
        return gameMode;
    }

    public void setGameMode(GameMode gameMode) {
        this.gameMode = gameMode;
    }

    public int getPing() {
        return ping;
    }

    public void setPing(int ping) {
        this.ping = ping;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public ArrayList<PlayerProperty> getProperties() {
        return properties;
    }

    public record PlayerProperty(String name, String value, boolean isSigned, String signature){}

    public enum GameMode {
        SURVIVAL(0, "survival"),
        CREATIVE(1, "creative"),
        ADVENTURE(2, "adventure"),
        SPECTATOR(3, "spectator");

        private final int id;
        private final String name;

        GameMode(int id, String name) {
            this.id = id;
            this.name = name;
        }

        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public static GameMode get(int id) {
            for (GameMode value : GameMode.values()) {
                if (value.id == id)
                    return value;
            }
            return null;
        }
    }
}
