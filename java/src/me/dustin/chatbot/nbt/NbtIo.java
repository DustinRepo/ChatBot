package me.dustin.chatbot.nbt;

import java.io.BufferedOutputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class NbtIo {

    public static void write(NbtCompound compound, File file) throws IOException {
        try (FileOutputStream fileOutputStream = new FileOutputStream(file);
             DataOutputStream dataOutputStream = new DataOutputStream(fileOutputStream);){
            NbtIo.write(compound, (DataOutput)dataOutputStream);
        }
    }

    public static NbtCompound read(File file) throws IOException {
        if (!file.exists()) {
            return null;
        }
        try (FileInputStream fileInputStream = new FileInputStream(file);){
            NbtCompound nbtCompound;
            try (DataInputStream dataInputStream = new DataInputStream(fileInputStream);){
                nbtCompound = NbtIo.read(dataInputStream);
            }
            return nbtCompound;
        }
    }

    public static NbtCompound read(DataInput input) throws IOException {
        NbtElement nbtElement = NbtIo.read(input, 0);
        if (nbtElement instanceof NbtCompound) {
            return (NbtCompound)nbtElement;
        }
        throw new IOException("Root tag must be a named compound tag");
    }

    public static void write(NbtCompound compound, DataOutput output) throws IOException {
        NbtIo.write((NbtElement)compound, output);
    }

    public static void write(NbtElement element, DataOutput output) throws IOException {
        output.writeByte(element.getType());
        if (element.getType() == 0) {
            return;
        }
        output.writeUTF("");
        element.write(output);
    }

    private static NbtElement read(DataInput input, int depth) throws IOException {
        byte b = input.readByte();
        if (b == 0) {
            return new NbtEnd();
        }
        NbtString.skip(input);
        return NbtElement.read(b, input, depth);
    }
}

