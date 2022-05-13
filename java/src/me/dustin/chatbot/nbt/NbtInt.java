package me.dustin.chatbot.nbt;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class NbtInt extends AbstractNbtNumber {
    private final int i;

    public NbtInt(int i) {
        this.i = i;
    }

    @Override
    public Object getValue() {
        return i;
    }

    @Override
    public JsonElement toJson() {
        return new JsonPrimitive(i);
    }

    public static NbtInt read(DataInput input, int depth) throws IOException {
        return new NbtInt(input.readInt());
    }

    @Override
    public void write(DataOutput output) throws IOException {
        output.writeInt(this.i);
    }

    @Override
    public long longValue() {
        return this.i;
    }

    @Override
    public int intValue() {
        return this.i;
    }

    @Override
    public short shortValue() {
        return (short)(this.i & 0xFFFF);
    }

    @Override
    public byte byteValue() {
        return (byte)(this.i & 0xFF);
    }

    @Override
    public double doubleValue() {
        return this.i;
    }

    @Override
    public float floatValue() {
        return this.i;
    }

    @Override
    public Number numberValue() {
        return this.i;
    }

    @Override
    public String toString() {
        return "NbtInt{" +
                "i=" + i +
                '}';
    }
}
