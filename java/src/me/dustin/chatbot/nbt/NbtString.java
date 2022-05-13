package me.dustin.chatbot.nbt;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class NbtString implements NbtElement {
    private final String string;

    public NbtString(String string) {
        this.string = string;
    }

    @Override
    public void write(DataOutput dataOutput) throws IOException {
        dataOutput.writeUTF(this.string);
    }

    @Override
    public JsonElement toJson() {
        return new JsonPrimitive(string);
    }

    @Override
    public Object getValue() {
        return string;
    }

    public static NbtElement read(DataInput input, int depth) throws IOException {
        return new NbtString(input.readUTF());
    }

    public static void skip(DataInput input) throws IOException {
        input.skipBytes(input.readUnsignedShort());
    }

    @Override
    public String toString() {
        return "NbtString{" +
                "string='" + string + '\'' +
                '}';
    }
}
