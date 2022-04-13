package me.dustin.chatbot.nbt;

import java.io.DataInput;
import java.io.IOException;

public class NbtEnd extends NbtElement {

    public static final NbtEnd END = new NbtEnd();

    public static NbtEnd read(DataInput input, int depth) throws IOException {
        return END;
    }

    @Override
    public Object getValue() {
        return null;
    }
}
