package me.dustin.chatbot.nbt;

import java.io.DataInput;
import java.io.IOException;

public abstract class NbtElement {
    private static final int END_TYPE = 0;
    private static final int BYTE_TYPE = 1;
    private static final int SHORT_TYPE = 2;
    private static final int INT_TYPE = 3;
    private static final int LONG_TYPE = 4;
    private static final int FLOAT_TYPE = 5;
    private static final int DOUBLE_TYPE = 6;
    private static final int BYTE_ARRAY_TYPE = 7;
    private static final int STRING_TYPE = 8;
    private static final int LIST_TYPE = 9;
    private static final int COMPOUND_TYPE = 10;
    private static final int INT_ARRAY_TYPE = 11;
    private static final int LONG_ARRAY_TYPE = 12;

    public static NbtElement read(int type, DataInput input, int depth) {
        try {
            switch (type) {
                case END_TYPE -> {
                    return new NbtEnd();
                }
                case BYTE_TYPE -> {
                    return NbtByte.read(input, depth);
                }
                case SHORT_TYPE -> {
                    return NbtShort.read(input, depth);
                }
                case INT_TYPE -> {
                    return NbtInt.read(input, depth);
                }
                case LONG_TYPE -> {
                    return NbtLong.read(input, depth);
                }
                case FLOAT_TYPE -> {
                    return NbtFloat.read(input, depth);
                }
                case DOUBLE_TYPE -> {
                    return NbtDouble.read(input, depth);
                }
                case BYTE_ARRAY_TYPE -> {
                    return NbtByteArray.read(input, depth);
                }
                case STRING_TYPE -> {
                    return NbtString.read(input, depth);
                }
                case LIST_TYPE -> {
                    return NbtList.read(input, depth);
                }
                case COMPOUND_TYPE -> {
                    return NbtCompound.read(input, depth);
                }
                case INT_ARRAY_TYPE -> {
                    return NbtIntArray.read(input, depth);
                }
                case LONG_ARRAY_TYPE -> {
                    return NbtLongArray.read(input, depth);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public abstract Object getValue();
}
