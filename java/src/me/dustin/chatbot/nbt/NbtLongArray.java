package me.dustin.chatbot.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Arrays;

public class NbtLongArray extends AbstractNbtList<NbtLong> {
    private long[] ls;

    public NbtLongArray(long[] ls) {
        this.ls = ls;
    }

    public static NbtLongArray read(DataInput input, int depth) throws IOException {
        int size = input.readInt();
        long[] ls = new long[size];
        for (int i = 0; i < size; i++)
            ls[i] = input.readLong();
        return new NbtLongArray(ls);
    }

    @Override
    public void write(DataOutput output) throws IOException {
        output.writeInt(this.ls.length);
        for (long l : this.ls) {
            output.writeLong(l);
        }
    }

    @Override
    public Object getValue() {
        return ls;
    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public String toString() {
        return "NbtLongArray{" +
                "ls=" + Arrays.toString(ls) +
                '}';
    }

    @Override
    public NbtLong get(int index) {
        return new NbtLong(this.ls[index]);
    }

    @Override
    public NbtLong set(int i, NbtLong nbtElement) {
        return null;
    }

    @Override
    public void add(int i, NbtLong nbtElement) {
        this.ls = add(this.ls, i, ((NbtLong)nbtElement).longValue());
    }

    @Override
    public NbtLong remove(int i) {
        long l = this.ls[i];
        this.ls = remove(this.ls, i);
        return new NbtLong(l);
    }

    @Override
    public boolean setElement(int index, NbtElement element) {
        if (element instanceof AbstractNbtNumber) {
            this.ls[index] = ((AbstractNbtNumber)element).longValue();
            return true;
        }
        return false;
    }

    @Override
    public boolean addElement(int index, NbtElement element) {
        if (element instanceof AbstractNbtNumber) {
            this.ls = add(this.ls, index, ((AbstractNbtNumber)element).longValue());
            return true;
        }
        return false;
    }

    @Override
    public byte getHeldType() {
        return LONG_TYPE;
    }
}
