package me.dustin.chatbot.nbt;

import java.io.DataInput;
import java.io.IOException;

public class NbtInt extends NbtElement {
    private final int i;

    public NbtInt(int i) {
        this.i = i;
    }

    @Override
    public Object getValue() {
        return i;
    }

    public static NbtInt read(DataInput input, int depth) throws IOException {
        return new NbtInt(input.readInt());
    }

    @Override
    public String toString() {
        return "NbtInt{" +
                "i=" + i +
                '}';
    }
}
