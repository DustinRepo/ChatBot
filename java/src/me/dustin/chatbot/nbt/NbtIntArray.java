package me.dustin.chatbot.nbt;

import java.io.DataInput;
import java.io.IOException;
import java.util.Arrays;

public class NbtIntArray extends NbtElement {
    private final int[] is;

    public NbtIntArray(int[] is) {
        this.is = is;
    }

    public static NbtIntArray read(DataInput input, int depth) throws IOException {
        int size = input.readInt();
        int[] is = new int[size];
        for (int i = 0; i < size; i++)
            is[i] = input.readInt();
        return new NbtIntArray(is);
    }

    @Override
    public Object getValue() {
        return is;
    }

    @Override
    public String toString() {
        return "NbtIntArray{" +
                "is=" + Arrays.toString(is) +
                '}';
    }
}
