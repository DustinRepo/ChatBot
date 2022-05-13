package me.dustin.chatbot.world.chunk;

import me.dustin.chatbot.ChatBot;
import me.dustin.chatbot.block.BlockEntity;
import me.dustin.chatbot.block.BlockPos;
import me.dustin.chatbot.block.BlockState;

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

    public BlockState getBlockState(BlockPos blockPos) {
        int sectionIndex = getSectionIndex(blockPos.getY());
        ChunkSection chunkSection;
        if (sectionIndex >= 0 && sectionIndex < chunkSections.size() && !(chunkSection = this.getChunkSections().get(sectionIndex)).isEmpty()) {
            return chunkSection.getBlockState(blockPos.getX() & 0xF, blockPos.getY() & 0xF, blockPos.getZ() & 0xF);
        }
        return null;
    }

    private int getSectionIndex(int y) {
        int bottomSectionY = ChatBot.getClientConnection().getWorld().getSectionCoord(ChatBot.getClientConnection().getWorld().getMinY());
        int coord = ChatBot.getClientConnection().getWorld().getSectionCoord(y);
        return coord - bottomSectionY;
    }
}
