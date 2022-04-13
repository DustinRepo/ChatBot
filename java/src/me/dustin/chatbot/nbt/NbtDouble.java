package me.dustin.chatbot.nbt;

import java.io.DataInput;
import java.io.IOException;

public class NbtDouble extends NbtElement {
    private final double d;

    public NbtDouble(double d) {
        this.d = d;
    }

    @Override
    public Object getValue() {
        return d;
    }

    public static NbtDouble read(DataInput input, int depth) throws IOException {
        return new NbtDouble(input.readDouble());
    }

    @Override
    public String toString() {
        return "NbtDouble{" +
                "d=" + d +
                '}';
    }
}
