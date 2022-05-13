package me.dustin.chatbot.nbt;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class NbtFloat extends AbstractNbtNumber {
    private final float f;

    public NbtFloat(float f) {
        this.f = f;
    }

    @Override
    public Object getValue() {
        return f;
    }

    @Override
    public JsonElement toJson() {
        return new JsonPrimitive(f);
    }

    public static NbtFloat read(DataInput input, int depth) throws IOException {
        return new NbtFloat(input.readFloat());
    }

    @Override
    public void write(DataOutput output) throws IOException {
        output.writeFloat(this.f);
    }

    @Override
    public long longValue() {
        return (long)this.f;
    }

    @Override
    public int intValue() {
        return floor(this.f);
    }

    @Override
    public short shortValue() {
        return (short)(floor(this.f) & 0xFFFF);
    }

    @Override
    public byte byteValue() {
        return (byte)(floor(this.f) & 0xFF);
    }

    @Override
    public double doubleValue() {
        return this.f;
    }

    @Override
    public float floatValue() {
        return this.f;
    }

    @Override
    public Number numberValue() {
        return Float.valueOf(this.f);
    }

    @Override
    public String toString() {
        return "NbtFloat{" +
                "f=" + f +
                '}';
    }
}
