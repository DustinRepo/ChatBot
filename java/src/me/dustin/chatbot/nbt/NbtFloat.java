package me.dustin.chatbot.nbt;

import java.io.DataInput;
import java.io.IOException;

public class NbtFloat extends NbtElement {
    private final float f;

    public NbtFloat(float f) {
        this.f = f;
    }

    @Override
    public Object getValue() {
        return f;
    }

    public static NbtFloat read(DataInput input, int depth) throws IOException {
        return new NbtFloat(input.readFloat());
    }

    @Override
    public String toString() {
        return "NbtFloat{" +
                "f=" + f +
                '}';
    }
}
