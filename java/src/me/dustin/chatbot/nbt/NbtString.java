package me.dustin.chatbot.nbt;

import java.io.DataInput;
import java.io.IOException;

public class NbtString extends NbtElement {
    private final String string;

    public NbtString(String string) {
        this.string = string;
    }

    @Override
    public Object getValue() {
        return string;
    }

    public static NbtElement read(DataInput input, int depth) throws IOException {
        return new NbtString(input.readUTF());
    }

    @Override
    public String toString() {
        return "NbtString{" +
                "string='" + string + '\'' +
                '}';
    }
}
