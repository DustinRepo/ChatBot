package me.dustin.chatbot.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class NbtEnd implements NbtElement {

    public static final NbtEnd END = new NbtEnd();

    public static NbtEnd read(DataInput input, int depth) throws IOException {
        return END;
    }

    @Override
    public void write(DataOutput dataOutput) throws IOException {

    }

    @Override
    public Object getValue() {
        return null;
    }
}
