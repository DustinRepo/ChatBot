package me.dustin.chatbot.network.world;

import me.dustin.chatbot.network.ClientConnection;
import me.dustin.chatbot.network.world.chunk.Chunk;

import java.util.ArrayList;

public class World {
    private final ClientConnection clientConnection;
    private final ArrayList<Chunk> chunks = new ArrayList<>();

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

    public BlockState getBlockState(int x, int y, int z) {
        int sectionCoord = y >> 4;
        int bottomSectionCoord = -64 >> 4;//TODO: find a way to determine bottom of world without hard-coding it
        int sectionIndex = sectionCoord - bottomSectionCoord;
        int chunkX = (int)(x / 16);
        int chunkZ = (int)(z / 16);
        Chunk chunk = getChunk(chunkX, chunkZ);
        if (chunk == null)
            return null;
        if (sectionIndex < 0 || sectionIndex > chunk.getChunkSections().size()) {
            System.out.println("Invalid section index! " + sectionIndex);
        }
        try {
            return chunk.getChunkSections().get(sectionIndex).getBlockState(x & 0xF, y & 0xF, z & 0xF);
        } catch (Exception e) {
            return null;
        }
    }

    public Chunk getChunk(int x, int z) {
        for (Chunk chunk : chunks) {
            if (chunk.getChunkX() == x && chunk.getChunkZ() == z)
                return chunk;
        }
        return null;
    }

    public void addChunk(Chunk chunk) {
        chunks.add(chunk);
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
