package me.dustin.chatbot.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class NbtShort extends AbstractNbtNumber {
    private final short s;

    public NbtShort(short s) {
        this.s = s;
    }

    @Override
    public Object getValue() {
        return s;
    }

    public static NbtShort read(DataInput input, int depth) throws IOException {
        return new NbtShort(input.readShort());
    }

    @Override
    public void write(DataOutput output) throws IOException {
        output.writeShort(this.s);
    }

    @Override
    public long longValue() {
        return this.s;
    }

    @Override
    public int intValue() {
        return this.s;
    }

    @Override
    public short shortValue() {
        return this.s;
    }

    @Override
    public byte byteValue() {
        return (byte)(this.s & 0xFF);
    }

    @Override
    public double doubleValue() {
        return this.s;
    }

    @Override
    public float floatValue() {
        return this.s;
    }

    @Override
    public Number numberValue() {
        return this.s;
    }

    @Override
    public String toString() {
        return "NbtShort{" +
                "s=" + s +
                '}';
    }
}
