package me.dustin.chatbot.nbt;

import java.io.DataInput;
import java.io.IOException;
import java.util.Arrays;

public class NbtLongArray extends NbtElement {
    private final long[] ls;

    public NbtLongArray(long[] ls) {
        this.ls = ls;
    }

    public static NbtLongArray read(DataInput input, int depth) throws IOException {
        int size = input.readInt();
        long[] ls = new long[size];
        for (int i = 0; i < size; i++)
            ls[i] = input.readLong();
        return new NbtLongArray(ls);
    }

    @Override
    public Object getValue() {
        return ls;
    }

    @Override
    public String toString() {
        return "NbtLongArray{" +
                "ls=" + Arrays.toString(ls) +
                '}';
    }
}
