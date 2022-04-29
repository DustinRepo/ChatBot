package me.dustin.chatbot.nbt;

import java.lang.reflect.Array;
import java.util.AbstractList;

public abstract class AbstractNbtList<T extends NbtElement> extends AbstractList<T> implements NbtElement {
    public AbstractNbtList() {
    }

    public abstract T set(int i, T nbtElement);

    public abstract void add(int i, T nbtElement);

    public abstract T remove(int i);

    public abstract boolean setElement(int index, NbtElement element);

    public abstract boolean addElement(int index, NbtElement element);

    public abstract byte getHeldType();

    public byte[] add(final byte[] array, final int index, final byte element) {
        return (byte[]) add(array, index, Byte.valueOf(element), Byte.TYPE);
    }

    public int[] add(final int[] array, final int index, final int element) {
        return (int[]) add(array, index, Integer.valueOf(element), Integer.TYPE);
    }

    public long[] add(final long[] array, final int index, final long element) {
        return (long[]) add(array, index, Long.valueOf(element), Long.TYPE);
    }

    private Object add(final Object array, final int index, final Object element, final Class<?> clss) {
        if (array == null) {
            if (index != 0) {
                throw new IndexOutOfBoundsException("Index: " + index + ", Length: 0");
            }
            final Object joinedArray = Array.newInstance(clss, 1);
            Array.set(joinedArray, 0, element);
            return joinedArray;
        }
        final int length = Array.getLength(array);
        if (index > length || index < 0) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Length: " + length);
        }
        final Object result = Array.newInstance(clss, length + 1);
        System.arraycopy(array, 0, result, 0, index);
        Array.set(result, index, element);
        if (index < length) {
            System.arraycopy(array, index, result, index + 1, length - index);
        }
        return result;
    }

    public byte[] remove(final byte[] array, final int index) {
        return (byte[]) remove((Object) array, index);
    }

    public int[] remove(final int[] array, final int index) {
        return (int[]) remove((Object) array, index);
    }

    public long[] remove(final long[] array, final int index) {
        return (long[]) remove((Object) array, index);
    }

    private Object remove(final Object array, final int index) {
        final int length = getLength(array);
        if (index < 0 || index >= length) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Length: " + length);
        }

        final Object result = Array.newInstance(array.getClass().getComponentType(), length - 1);
        System.arraycopy(array, 0, result, 0, index);
        if (index < length - 1) {
            System.arraycopy(array, index + 1, result, index, length - index - 1);
        }

        return result;
    }

    private int getLength(final Object array) {
        if (array == null) {
            return 0;
        }
        return Array.getLength(array);
    }
}
