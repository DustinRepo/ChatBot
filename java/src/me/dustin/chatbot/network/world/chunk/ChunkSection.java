package me.dustin.chatbot.network.world.chunk;

import me.dustin.chatbot.network.world.BlockState;

public class ChunkSection {
    private final int blockCount;
    private final Palette palette;

    public ChunkSection(int blockCount, Palette palette) {
        this.blockCount = blockCount;
        this.palette = palette;
    }

    public int getBlockCount() {
        return blockCount;
    }

    public Palette getPalette() {
        return palette;
    }

    public BlockState getBlockState(int x, int y, int z) {
        return (BlockState)getPalette().get(getPalette().computeIndex(x, y, z));
    }
}
