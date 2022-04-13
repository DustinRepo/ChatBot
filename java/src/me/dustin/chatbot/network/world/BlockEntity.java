package me.dustin.chatbot.network.world;

import me.dustin.chatbot.nbt.NbtElement;

public class BlockEntity {
    private final int x, y, z;
    private final int type;
    private final NbtElement nbt;

    public BlockEntity(int x, int y, int z, int type, NbtElement nbt) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.type = type;
        this.nbt = nbt;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }

    public int getType() {
        return type;
    }

    public NbtElement getNbt() {
        return nbt;
    }

    @Override
    public String toString() {
        return "BlockEntity{" +
                "x=" + x +
                ", y=" + y +
                ", z=" + z +
                ", type=" + type +
                ", nbt=" + nbt +
                '}';
    }
}
