package me.dustin.chatbot.nbt;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

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
        Gson prettyGson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
        return "NbtCompound: " + prettyGson.toJson(toJson());
    }

    public Map<String, NbtElement> getElements() {
        return elements;
    }

    public boolean has(String element) {
        return getElements().containsKey(element);
    }

    @Override
    public Object getValue() {
        return elements;
    }

    public NbtElement get(String element) {
        return getElements().get(element);
    }

    @Override
    public JsonElement toJson() {
        JsonObject o = new JsonObject();
        for (String s1 : elements.keySet()) {
            NbtElement nbtElement = elements.get(s1);
            o.add(s1, nbtElement.toJson());
        }
        return o;
    }
}
