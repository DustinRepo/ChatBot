package me.dustin.chatbot.nbt;


public abstract class AbstractNbtNumber implements NbtElement {
    protected AbstractNbtNumber() {
    }
    public abstract long longValue();
    public abstract int intValue();
    public abstract short shortValue();
    public abstract byte byteValue();
    public abstract double doubleValue();
    public abstract float floatValue();
    public abstract Number numberValue();

    public int floor(double value) {
        int i = (int)value;
        return value < (double)i ? i - 1 : i;
    }
}

