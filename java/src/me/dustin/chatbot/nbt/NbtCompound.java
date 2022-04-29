package me.dustin.chatbot.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class NbtCompound implements NbtElement {

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
    public void write(DataOutput output) throws IOException {
        for (String string : this.elements.keySet()) {
            NbtElement nbtElement = this.elements.get(string);
            NbtCompound.write(string, nbtElement, output);
        }
        output.writeByte(0);
    }

    private static void write(String key, NbtElement element, DataOutput output) throws IOException {
        output.writeByte(element.getType());
        if (element.getType() == 0) {
            return;
        }
        output.writeUTF(key);
        element.write(output);
    }

    public void put(String name, NbtElement element) {
        elements.put(name, element);
    }

    @Override
    public String toString() {
        return "NbtCompound{" +
                "elements=" + elements +
                '}';
    }

    public Map<String, NbtElement> getElements() {
        return elements;
    }

    @Override
    public Object getValue() {
        return elements;
    }
}
