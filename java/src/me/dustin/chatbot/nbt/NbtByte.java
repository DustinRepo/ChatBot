package me.dustin.chatbot.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class NbtByte extends AbstractNbtNumber {
    private final byte b;

    public NbtByte(byte b) {
        this.b = b;
    }
    
    public static NbtByte read(DataInput input, int depth) throws IOException {
        return new NbtByte(input.readByte());
    }
    @Override
    public void write(DataOutput output) throws IOException {
        output.writeByte(this.b);
    }

    @Override
    public Object getValue() {
        return b;
    }

    @Override
    public String toString() {
        return "NbtByte{" +
                "b=" + b +
                '}';
    }

    @Override
    public long longValue() {
        return b;
    }

    @Override
    public int intValue() {
        return b;
    }

    @Override
    public short shortValue() {
        return b;
    }

    @Override
    public byte byteValue() {
        return b;
    }

    @Override
    public double doubleValue() {
        return b;
    }

    @Override
    public float floatValue() {
        return b;
    }

    @Override
    public Number numberValue() {
        return b;
    }
}
