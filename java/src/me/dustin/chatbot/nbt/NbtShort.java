package me.dustin.chatbot.nbt;

import java.io.DataInput;
import java.io.IOException;

public class NbtShort extends NbtElement {
    private final short s;

    public NbtShort(short s) {
        this.s = s;
    }

    @Override
    public Object getValue() {
        return s;
    }

    public static NbtShort read(DataInput input, int depth) throws IOException {
        return new NbtShort(input.readShort());
    }

    @Override
    public String toString() {
        return "NbtShort{" +
                "s=" + s +
                '}';
    }
}
