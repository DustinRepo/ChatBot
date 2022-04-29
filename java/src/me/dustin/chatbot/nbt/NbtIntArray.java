package me.dustin.chatbot.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Arrays;

public class NbtIntArray extends AbstractNbtList {
    private int[] is;

    public NbtIntArray(int[] is) {
        this.is = is;
    }

    public static NbtIntArray read(DataInput input, int depth) throws IOException {
        int size = input.readInt();
        int[] is = new int[size];
        for (int i = 0; i < size; i++)
            is[i] = input.readInt();
        return new NbtIntArray(is);
    }

    @Override
    public void write(DataOutput output) throws IOException {
        output.writeInt(this.is.length);
        for (int i : this.is) {
            output.writeInt(i);
        }
    }

    @Override
    public Object getValue() {
        return is;
    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public String toString() {
        return "NbtIntArray{" +
                "is=" + Arrays.toString(is) +
                '}';
    }

    @Override
    public NbtElement set(int i, NbtElement nbtElement) {
        int j = this.is[i];
        this.is[i] = ((NbtInt)nbtElement).intValue();
        return new NbtInt(j);
    }

    @Override
    public void add(int i, NbtElement nbtElement) {
        this.is = add(this.is, i, ((NbtInt)nbtElement).intValue());
    }

    @Override
    public NbtInt get(int i) {
        return new NbtInt(this.is[i]);
    }

    @Override
    public NbtElement remove(int i) {
        int j = this.is[i];
        this.is = remove(this.is, i);
        return new NbtInt(j);
    }

    @Override
    public boolean setElement(int index, NbtElement element) {
        if (element instanceof AbstractNbtNumber) {
            this.is[index] = ((AbstractNbtNumber)element).intValue();
            return true;
        }
        return false;
    }

    @Override
    public boolean addElement(int index, NbtElement element) {
        if (element instanceof AbstractNbtNumber) {
            this.is = add(this.is, index, ((AbstractNbtNumber)element).intValue());
            return true;
        }
        return false;
    }

    @Override
    public byte getHeldType() {
        return INT_TYPE;
    }
}
