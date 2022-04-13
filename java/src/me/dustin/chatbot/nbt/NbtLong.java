package me.dustin.chatbot.nbt;

import java.io.DataInput;
import java.io.IOException;

public class NbtLong extends NbtElement {
    private final long l;

    public NbtLong(long l) {
        this.l = l;
    }

    @Override
    public Object getValue() {
        return l;
    }

    public static NbtElement read(DataInput input, int depth) throws IOException {
        return new NbtLong(input.readLong());
    }

    @Override
    public String toString() {
        return "NbtLong{" +
                "l=" + l +
                '}';
    }
}
