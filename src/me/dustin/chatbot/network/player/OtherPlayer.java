package me.dustin.chatbot.network.player;

import java.util.UUID;

public class OtherPlayer {

    public String name;
    public UUID uuid;
    public int gameMode;
    public int ping;
    public String displayName;

    public PlayerProperty properties;

    public OtherPlayer(String name, UUID uuid) {
        this.name = name;
        this.uuid = uuid;
        displayName = "";
    }

    public record PlayerProperty(String name, String value, String signature){}
}
