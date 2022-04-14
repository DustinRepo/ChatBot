package me.dustin.chatbot.world;

import me.dustin.chatbot.entity.LivingEntity;
import me.dustin.chatbot.entity.player.PlayerEntity;
import me.dustin.chatbot.network.ClientConnection;
import me.dustin.chatbot.world.chunk.Chunk;

import java.util.ArrayList;

public class World {
    private final ClientConnection clientConnection;
    private final ArrayList<Chunk> chunks = new ArrayList<>();
    private final ArrayList<LivingEntity> livingEntities = new ArrayList<>();
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

    public LivingEntity getEntity(int id) {
        for (LivingEntity livingEntity : livingEntities) {
            if (livingEntity.getEntityId() == id)
                return livingEntity;
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

    public ArrayList<LivingEntity> getLivingEntities() {
        return livingEntities;
    }

    public ArrayList<PlayerEntity> getPlayerEntities() {
        return playerEntities;
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
    }
}
