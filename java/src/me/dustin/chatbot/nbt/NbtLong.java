package me.dustin.chatbot.nbt;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class NbtLong extends AbstractNbtNumber {
    private final long l;

    public NbtLong(long l) {
        this.l = l;
    }

    @Override
    public Object getValue() {
        return l;
    }

    public static NbtElement read(DataInput input, int depth) throws IOException {
        return new NbtLong(input.readLong());
    }

    @Override
    public void write(DataOutput output) throws IOException {
        output.writeLong(this.l);
    }

    @Override
    public JsonElement toJson() {
        return new JsonPrimitive(l);
    }

    @Override
    public long longValue() {
        return this.l;
    }

    @Override
    public int intValue() {
        return (int)(this.l & 0xFFFFFFFFFFFFFFFFL);
    }

    @Override
    public short shortValue() {
        return (short)(this.l & 0xFFFFL);
    }

    @Override
    public byte byteValue() {
        return (byte)(this.l & 0xFFL);
    }

    @Override
    public double doubleValue() {
        return this.l;
    }

    @Override
    public float floatValue() {
        return this.l;
    }

    @Override
    public Number numberValue() {
        return this.l;
    }

    @Override
    public String toString() {
        return "NbtLong{" +
                "l=" + l +
                '}';
    }
}
