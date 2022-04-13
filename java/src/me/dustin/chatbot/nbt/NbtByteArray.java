package me.dustin.chatbot.nbt;

import java.io.DataInput;
import java.io.IOException;
import java.util.Arrays;

public class NbtByteArray extends NbtElement {
    private final byte[] bs;

    public NbtByteArray(byte[] bs) {
        this.bs = bs;
    }

    @Override
    public Object getValue() {
        return bs;
    }

    public static NbtByteArray read(DataInput input, int depth) throws IOException {
        int size = input.readInt();
        byte[] bs = new byte[size];
        input.readFully(bs);
        return new NbtByteArray(bs);
    }

    @Override
    public String toString() {
        return "NbtByteArray{" +
                "bs=" + Arrays.toString(bs) +
                '}';
    }
}
