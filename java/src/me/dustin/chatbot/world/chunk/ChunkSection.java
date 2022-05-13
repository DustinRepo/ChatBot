package me.dustin.chatbot.world.chunk;

import me.dustin.chatbot.block.BlockState;

public class ChunkSection {
    private final int nonEmptyBlockCount;
    private final Palette palette;

    public ChunkSection(int nonEmptyBlockCount, Palette palette) {
        this.nonEmptyBlockCount = nonEmptyBlockCount;
        this.palette = palette;
    }

    public int getNonEmptyBlockCount() {
        return nonEmptyBlockCount;
    }

    public Palette getPalette() {
        return palette;
    }

    public BlockState getBlockState(int x, int y, int z) {
        return (BlockState)getPalette().get(getPalette().computeIndex(x, y, z));
    }

    public boolean isEmpty() {
        return nonEmptyBlockCount == 0;
    }
}
