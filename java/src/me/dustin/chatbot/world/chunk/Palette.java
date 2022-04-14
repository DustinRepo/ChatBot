package me.dustin.chatbot.world.chunk;

import me.dustin.chatbot.network.packet.pipeline.PacketByteBuf;
import me.dustin.chatbot.world.BlockState;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public interface Palette {

    byte getBitsPerBlock();
    void read(PacketByteBuf packetByteBuf);
    int computeIndex(int x, int y, int z);
    int idForState(BlockState state);
    BlockState stateForId(int id);
    Object get(int index);

    static Palette choosePalette(byte bitsPerBlock) {
        if (bitsPerBlock == 0) {
            return new SingleValuePalette(bitsPerBlock);
        } else if (bitsPerBlock <= 4) {
            return new IndirectPalette((byte) 4);
        } else if (bitsPerBlock <= 8) {
            return new IndirectPalette(bitsPerBlock);
        } else {
            return new DirectPalette();
        }
    }

    class SingleValuePalette implements Palette {
        private final byte bitsPerBlock;
        public SingleValuePalette(byte bitsPerBlock) {
            this.bitsPerBlock = bitsPerBlock;
        }
        @Override
        public byte getBitsPerBlock() {
            return bitsPerBlock;
        }

        @Override
        public void read(PacketByteBuf packetByteBuf) {
            int value = packetByteBuf.readVarInt();
        }

        @Override
        public int computeIndex(int x, int y, int z)  {
            return (y << this.getBitsPerBlock() | z) << this.getBitsPerBlock() | x;
        }

        @Override
        public int idForState(BlockState state) {
            return state.getBlockStateData().get(0).getStateId();
        }

        @Override
        public BlockState stateForId(int id) {
            return BlockState.get(id);
        }

        @Override
        public Object get(int index) {
            return null;
        }
    }

    class IndirectPalette implements Palette {
        private final ArrayList<BlockState> states = new ArrayList<>();
        private final Map<Integer, BlockState> idToState = new HashMap<>();
        private final Map<BlockState, Integer> stateToId = new HashMap<>();
        private final byte bitsPerBlock;

        public IndirectPalette(byte bitsPerBlock) {
            this.bitsPerBlock = bitsPerBlock;
        }

        public void read(PacketByteBuf byteBuf) {
            int length = byteBuf.readVarInt();
            for (int id = 0; id < length; id++) {
                int stateId = byteBuf.readVarInt();
                BlockState blockState = BlockState.get(stateId);
                if (blockState != null) {
                    idToState.put(stateId, blockState);
                    stateToId.put(blockState, stateId);
                    states.add(blockState);
                }
            }
        }

        @Override
        public int computeIndex(int x, int y, int z)  {
            return (y << this.getBitsPerBlock() | z) << this.getBitsPerBlock() | x;
        }

        @Override
        public int idForState(BlockState state) {
            return stateToId.get(state);
        }

        @Override
        public BlockState stateForId(int id) {
            return idToState.get(id);
        }

        @Override
        public Object get(int index) {
            return states.get(index);
        }

        @Override
        public byte getBitsPerBlock() {
            return bitsPerBlock;
        }
    }

    class DirectPalette implements Palette {

        @Override
        public int computeIndex(int x, int y, int z)  {
            return (y << this.getBitsPerBlock() | z) << this.getBitsPerBlock() | x;
        }

        @Override
        public byte getBitsPerBlock() {
            return 15;//Ceil(Log2(BlockState.TotalNumberOfStates));
        }

        @Override
        public void read(PacketByteBuf packetByteBuf) {

        }

        @Override
        public int idForState(BlockState state) {
            return state.getBlockStateData().get(0).getStateId();
        }

        @Override
        public BlockState stateForId(int id) {
            return BlockState.get(id);
        }

        @Override
        public Object get(int index) {
            return null;
        }
    }
}
