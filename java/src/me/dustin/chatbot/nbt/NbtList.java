package me.dustin.chatbot.nbt;

import com.google.common.collect.Lists;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class NbtList implements NbtElement {
    private final List<NbtElement> elements;
    private int type;

    public NbtList(List<NbtElement> elements, int type) {
        this.elements = elements;
        this.type = type;
    }

    public NbtList() {
        this.elements = new ArrayList<>();
        this.type = 0;
    }

    @Override
    public Object getValue() {
        return elements;
    }

    @Override
    public void write(DataOutput output) throws IOException {
        this.type = this.elements.isEmpty() ? (byte)0 : this.elements.get(0).getType();
        output.writeByte(this.type);
        output.writeInt(this.elements.size());
        for (NbtElement nbtElement : this.elements) {
            nbtElement.write(output);
        }
    }

    public static NbtList read(DataInput input, int depth) throws IOException {
        int type = input.readByte();
        int listSize = input.readInt();
        if (type == 0 && listSize > 0)
            throw new RuntimeException("Nbt List missing type");
        List<NbtElement> list = Lists.newArrayListWithCapacity(listSize);
        for (int i = 0; i < listSize; i++) {
            list.add(NbtElement.read(type, input, depth + 1));
        }
        return new NbtList(list, type);
    }

    @Override
    public String toString() {
        return "NbtList{" +
                "elements=" + elements +
                ", type=" + type +
                '}';
    }
}
