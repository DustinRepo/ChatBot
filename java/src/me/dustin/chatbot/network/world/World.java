package me.dustin.chatbot.network.world;

import me.dustin.chatbot.network.ClientConnection;

public class World {
    private final ClientConnection clientConnection;

    private Difficulty difficulty = Difficulty.PEACEFUL;

    public World(ClientConnection clientConnection) {
        this.clientConnection = clientConnection;
    }

    public void setDifficulty(Difficulty difficulty) {
        this.difficulty = difficulty;
    }

    public Difficulty getDifficulty() {
        return difficulty;
    }

    public ClientConnection getClientConnection() {
        return clientConnection;
    }

    public enum Difficulty {
        PEACEFUL(0, "Peaceful"),
        EASY(1, "Easy"),
        NORMAL(2, "Normal"),
        HARD(3, "Hard");
        final int id;
        final String name;

        Difficulty(int id, String name) {
            this.id = id;
            this.name = name;
        }

        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }
    }
}
