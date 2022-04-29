package me.dustin.chatbot.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.Arrays;

public class NbtByteArray extends AbstractNbtList<NbtByte> {
    private byte[] bs;

    public NbtByteArray(byte[] bs) {
        this.bs = bs;
    }

    @Override
    public Object getValue() {
        return bs;
    }

    public static NbtByteArray read(DataInput input, int depth) throws IOException {
        int size = input.readInt();
        byte[] bs = new byte[size];
        input.readFully(bs);
        return new NbtByteArray(bs);
    }
    @Override
    public void write(DataOutput output) throws IOException {
        output.writeInt(this.bs.length);
        output.write(this.bs);
    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public String toString() {
        return "NbtByteArray{" +
                "bs=" + Arrays.toString(bs) +
                '}';
    }

    @Override
    public NbtByte get(int i) {
        return new NbtByte(this.bs[i]);
    }

    @Override
    public NbtByte set(int i, NbtByte nbtElement) {
        byte b = this.bs[i];
        this.bs[i] = nbtElement.byteValue();
        return new NbtByte(b);
    }

    @Override
    public void add(int i, NbtByte nbtElement) {
        this.bs = add(this.bs, i, nbtElement.byteValue());
    }

    @Override
    public NbtByte remove(int i) {
        byte b = this.bs[i];
        this.bs = remove(this.bs, i);
        return new NbtByte(b);
    }

    @Override
    public boolean setElement(int index, NbtElement element) {
        if (element instanceof AbstractNbtNumber) {
            this.bs[index] = ((AbstractNbtNumber)element).byteValue();
            return true;
        }
        return false;
    }

    @Override
    public boolean addElement(int index, NbtElement element) {
        if (element instanceof AbstractNbtNumber) {
            this.bs = add(this.bs, index, ((AbstractNbtNumber)element).byteValue());
            return true;
        }
        return false;
    }

    @Override
    public byte getHeldType() {
        return BYTE_TYPE;
    }
}
