package me.dustin.chatbot.world.chunk;

import me.dustin.chatbot.world.BlockEntity;

import java.util.ArrayList;

public class Chunk {
    private final int chunkX, chunkZ;
    private final ArrayList<ChunkSection> chunkSections = new ArrayList<>();
    private final ArrayList<BlockEntity> blockEntities = new ArrayList<>();

    public Chunk(int chunkX, int chunkZ) {
        this.chunkX = chunkX;
        this.chunkZ = chunkZ;
    }

    public void addBlockEntity(BlockEntity blockEntity) {
        blockEntities.add(blockEntity);
    }

    public void removeBlockEntity(BlockEntity blockEntity) {
        blockEntities.remove(blockEntity);
    }

    public void addChunkSection(ChunkSection chunkSection) {
        chunkSections.add(chunkSection);
    }

    public ArrayList<ChunkSection> getChunkSections() {
        return chunkSections;
    }

    public int getChunkX() {
        return chunkX;
    }

    public int getChunkZ() {
        return chunkZ;
    }
}
