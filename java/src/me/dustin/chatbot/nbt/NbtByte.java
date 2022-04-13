package me.dustin.chatbot.nbt;

import java.io.DataInput;
import java.io.IOException;

public class NbtByte extends NbtElement {
    private final byte b;

    public NbtByte(byte b) {
        this.b = b;
    }
    
    public static NbtByte read(DataInput input, int depth) throws IOException {
        return new NbtByte(input.readByte());
    }

    @Override
    public Object getValue() {
        return b;
    }

    @Override
    public String toString() {
        return "NbtByte{" +
                "b=" + b +
                '}';
    }
}
