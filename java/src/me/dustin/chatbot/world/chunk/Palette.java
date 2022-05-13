package me.dustin.chatbot.world.chunk;

import me.dustin.chatbot.network.packet.pipeline.PacketByteBuf;
import me.dustin.chatbot.block.BlockState;

import java.util.HashMap;
import java.util.Map;

public interface Palette {

    byte getBitsPerBlock();
    int computeIndex(int x, int y, int z);
    Object get(int index);
    void fromPacket(PacketByteBuf packetByteBuf);

    static Palette choosePalette(byte bitsPerBlock) {
        if (bitsPerBlock == 0) {
            return new SingleValuePalette(bitsPerBlock);
        } else if (bitsPerBlock <= 4) {
            return new ArrayPalette((byte) 4);
        } else if (bitsPerBlock <= 8) {
            return new MapPalette(bitsPerBlock);
        } else {
            return new ListPalette();
        }
    }

    class SingleValuePalette implements Palette {
        private final byte bitsPerBlock;
        private BlockState blockState;
        public SingleValuePalette(byte bitsPerBlock) {
            this.bitsPerBlock = bitsPerBlock;
        }
        @Override
        public byte getBitsPerBlock() {
            return bitsPerBlock;
        }

        @Override
        public int computeIndex(int x, int y, int z)  {
            return (y << 4 | z) << 4 | x;
        }

        @Override
        public Object get(int index) {
            return blockState;
        }

        @Override
        public void fromPacket(PacketByteBuf packetByteBuf) {
            this.blockState = BlockState.get(packetByteBuf.readVarInt());
        }
    }

    class ArrayPalette implements Palette {
        private int size;
        private final BlockState[] blockStates;
        private final byte bitsPerBlock;

        public ArrayPalette(byte bitsPerBlock) {
            blockStates = new BlockState[1 << bitsPerBlock];
            this.bitsPerBlock = bitsPerBlock;
        }

        @Override
        public int computeIndex(int x, int y, int z)  {
            return (y << 4 | z) << 4 | x;
        }

        @Override
        public Object get(int index) {
            if (index >= 0 && index < this.size) {
                return this.blockStates[index];
            }
            return null;
        }

        @Override
        public void fromPacket(PacketByteBuf packetByteBuf) {
            size = packetByteBuf.readVarInt();
            for (int i = 0; i < size; i++)
                blockStates[i] = BlockState.get(packetByteBuf.readVarInt());
        }

        @Override
        public byte getBitsPerBlock() {
            return bitsPerBlock;
        }
    }

    class MapPalette implements Palette {
        private final Map<Integer, BlockState> map = new HashMap<>();
        private final byte bitsPerBlock;

        public MapPalette(byte bitsPerBlock) {
            this.bitsPerBlock = bitsPerBlock;
        }

        @Override
        public int computeIndex(int x, int y, int z)  {
            return (y << 4 | z) << 4 | x;
        }

        @Override
        public byte getBitsPerBlock() {
            return bitsPerBlock;
        }

        @Override
        public Object get(int index) {
            return map.get(index);
        }

        @Override
        public void fromPacket(PacketByteBuf packetByteBuf) {
            map.clear();
            int size = packetByteBuf.readVarInt();
            for (int i = 0; i < size; i++) {
                this.map.put(i, BlockState.get(packetByteBuf.readVarInt()));
            }
        }
    }

    class ListPalette implements Palette{

        @Override
        public byte getBitsPerBlock() {
            return (byte) ceilLog2(BlockState.totalNumber());
        }

        @Override
        public int computeIndex(int x, int y, int z) {
            return 0;
        }

        @Override
        public Object get(int index) {
            return BlockState.get(index);
        }

        @Override
        public void fromPacket(PacketByteBuf packetByteBuf) {

        }

        private final int[] MULTIPLY_DE_BRUIJN_BIT_POSITION = new int[]{0, 1, 28, 2, 29, 14, 24, 3, 30, 22, 20, 15, 25, 17, 4, 8, 31, 27, 13, 23, 21, 19, 16, 7, 26, 12, 18, 6, 11, 5, 10, 9};
        private int ceilLog2(int value) {
            value = isPowerOfTwo(value) ? value : smallestEncompassingPowerOfTwo(value);
            return MULTIPLY_DE_BRUIJN_BIT_POSITION[(int)((long)value * 125613361L >> 27) & 0x1F];
        }

        private int smallestEncompassingPowerOfTwo(int value) {
            int i = value - 1;
            i |= i >> 1;
            i |= i >> 2;
            i |= i >> 4;
            i |= i >> 8;
            i |= i >> 16;
            return i + 1;
        }

        private boolean isPowerOfTwo(int value) {
            return value != 0 && (value & value - 1) == 0;
        }
    }
}
