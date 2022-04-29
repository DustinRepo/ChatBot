package me.dustin.chatbot.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class NbtDouble extends AbstractNbtNumber {
    private final double d;

    public NbtDouble(double d) {
        this.d = d;
    }

    @Override
    public Object getValue() {
        return d;
    }

    public static NbtDouble read(DataInput input, int depth) throws IOException {
        return new NbtDouble(input.readDouble());
    }

    @Override
    public void write(DataOutput output) throws IOException {
        output.writeDouble(this.d);
    }

    @Override
    public String toString() {
        return "NbtDouble{" +
                "d=" + d +
                '}';
    }

    @Override
    public long longValue() {
        return (long)Math.floor(this.d);
    }

    @Override
    public int intValue() {
        return floor(this.d);
    }

    @Override
    public short shortValue() {
        return (short)(floor(this.d) & 0xFFFF);
    }

    @Override
    public byte byteValue() {
        return (byte)(floor(this.d) & 0xFF);
    }

    @Override
    public double doubleValue() {
        return this.d;
    }

    @Override
    public float floatValue() {
        return (float)this.d;
    }

    @Override
    public Number numberValue() {
        return this.d;
    }
}
