package me.dustin.chatbot.nbt;

import java.io.DataInput;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class NbtCompound extends NbtElement {

    private final Map<String, NbtElement> elements;

    public NbtCompound(Map<String, NbtElement> elements) {
        this.elements = elements;
    }

    public NbtCompound() {
        this(new HashMap<>());
    }

    public static NbtElement read(DataInput input, int depth) throws IOException {
        Map<String, NbtElement> elements = new HashMap<>();
        byte b;
        while((b = input.readByte()) != 0) {
            String s = input.readUTF();
            NbtElement element = NbtElement.read(b, input, depth);
            elements.put(s, element);
        }
        return new NbtCompound(elements);
    }

    @Override
    public String toString() {
        return "NbtCompound{" +
                "elements=" + elements +
                '}';
    }

    @Override
    public Object getValue() {
        return elements;
    }
}
