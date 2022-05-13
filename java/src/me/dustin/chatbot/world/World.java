package me.dustin.chatbot.world;

import me.dustin.chatbot.block.BlockPos;
import me.dustin.chatbot.block.BlockState;
import me.dustin.chatbot.entity.Entity;
import me.dustin.chatbot.entity.player.PlayerEntity;
import me.dustin.chatbot.network.ClientConnection;
import me.dustin.chatbot.world.chunk.Chunk;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class World {
    private final ClientConnection clientConnection;
    private final ArrayList<Chunk> chunks = new ArrayList<>();
    private final ArrayList<Entity> entities = new ArrayList<>();
    private final ArrayList<PlayerEntity> playerEntities = new ArrayList<>();

    private Difficulty difficulty = Difficulty.PEACEFUL;
    private Dimension dimension;

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

    public BlockState getBlockState(BlockPos blockPos) {
        int chunkX = getSectionCoord(blockPos.getX());
        int chunkZ = getSectionCoord(blockPos.getZ());
        Chunk chunk = getChunk(chunkX, chunkZ);
        return chunk.getBlockState(blockPos);
    }

    public Entity getEntity(int id) {
        for (Entity entity : entities) {
            if (entity.getEntityId() == id)
                return entity;
        }
        return null;
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

    public Dimension getDimension() {
        return dimension;
    }

    public void setDimension(Dimension dimension) {
        this.dimension = dimension;
    }

    public ArrayList<Entity> getEntities() {
        return entities;
    }

    public ArrayList<PlayerEntity> getPlayerEntities() {
        return playerEntities;
    }

    public int getMinY() {
        if (getDimension() == null)
            return 0;
        return getDimension().getMinY();
    }

    public void setMinY(Dimension dimension, int minY) {
        if (dimension == null)
            return;
        dimension.setMinY(minY);
    }

    public int getWorldHeight() {
        if (getDimension() == null)
            return 256;
        return getDimension().getWorldHeight();
    }

    public void setWorldHeight(Dimension dimension, int worldHeight) {
        if (dimension == null)
            return;
        dimension.setWorldHeight(worldHeight);
    }

    public int countVerticalSections() {
        Dimension dimension = getDimension();
        int topY = dimension.getWorldHeight() - dimension.getMinY();
        int topSectionY = getSectionCoord(topY - 1) + 1;
        int bottomSectionY = getSectionCoord(dimension.getMinY());
        return topSectionY - bottomSectionY;
    }

    public int getSectionCoord(int coord) {
        return coord >> 4;
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

    public enum Dimension {
        NETHER("minecraft:nether", -1),
        OVERWORLD("minecraft:overworld", 0),
        END("minecraft:the_end", 1),
        CUSTOM("", 2);

        private String name;
        private final int id;

        private int min_y = 0;
        private int world_height = 256;

        Dimension(String name, int id) {
            this.name = name;
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public int getId() {
            return id;
        }

        public static Dimension get(String name) {
            for (Dimension value : Dimension.values()) {
                if (value.getName().equalsIgnoreCase(name))
                    return value;
            }
            Dimension dimension = CUSTOM;
            dimension.name = name;
            return dimension;
        }

        public static Dimension get(int id) {
            for (Dimension value : Dimension.values()) {
                if (value.getId() == id)
                    return value;
            }
            return CUSTOM;
        }
        public int getMinY() {
            return min_y;
        }

        public void setMinY(int minY) {
            this.min_y = minY;
        }

        public int getWorldHeight() {
            return world_height;
        }

        public void setWorldHeight(int worldHeight) {
            this.world_height = worldHeight;
        }
    }
}
