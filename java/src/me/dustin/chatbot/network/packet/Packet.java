package me.dustin.chatbot.network.packet;

import com.google.common.io.ByteArrayDataOutput;
import me.dustin.chatbot.network.ClientConnection;
import me.dustin.chatbot.network.packet.handler.ClientBoundPacketHandler;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class Packet {

    private ClientConnection clientConnection;

    public ByteArrayDataOutput createPacket() throws IOException {
        return null;
    }

    public String readString(DataInputStream dataInputStream) throws IOException {
        int strSize = readVarInt(dataInputStream);
        byte[] strBytes = new byte[strSize];
        dataInputStream.readFully(strBytes, 0, strSize);
        return new String(strBytes);
    }

    public void writeString(DataOutputStream dataOutputStream, String string) throws IOException {
        byte[] strBytes = string.getBytes(StandardCharsets.UTF_8);
        int size = strBytes.length;
        writeVarInt(dataOutputStream, size);
        dataOutputStream.write(strBytes);
    }

    public UUID readUUID(DataInputStream dataInputStream) throws IOException {
        long first = dataInputStream.readLong();
        long second = dataInputStream.readLong();
        return new UUID(first, second);
    }

    public ClientConnection getClientConnection() {
        return clientConnection;
    }

    public void setClientConnection(ClientConnection clientConnection) {
        this.clientConnection = clientConnection;
    }

    public static abstract class ClientBoundPacket extends Packet {
        protected final ClientBoundPacketHandler clientBoundPacketHandler;

        public ClientBoundPacket(ClientBoundPacketHandler clientBoundPacketHandler) {
            this.clientBoundPacketHandler = clientBoundPacketHandler;
        }

        public abstract void createPacket(DataInputStream dataInputStream) throws IOException;

        public abstract void apply();
    }

    public static void writeVarInt(DataOutputStream out, int paramInt) throws IOException {
        while (true) {
            if ((paramInt & 0xFFFFFF80) == 0) {
                out.write(paramInt);
                return;
            }

            out.write(paramInt & 0x7F | 0x80);
            paramInt >>>= 7;
        }
    }

    public static void writeVarInt(ByteArrayDataOutput out, int paramInt) {
        while (true) {
            if ((paramInt & 0xFFFFFF80) == 0) {
                out.write(paramInt);
                return;
            }

            out.write(paramInt & 0x7F | 0x80);
            paramInt >>>= 7;
        }
    }

    public static int sizeOfVarInt(int paramInt) {
        int result = 0;
        do {
            result++;
            paramInt >>>= 7;
        } while (paramInt != 0);
        return result;
    }

    public static int readVarInt(DataInputStream in) throws IOException {
        int i = 0;
        int j = 0;
        while (true) {
            int k = in.readByte();

            i |= (k & 0x7F) << j++ * 7;

            if (j > 5) {
                throw new RuntimeException("VarInt too big");
            }

            if ((k & 0x80) != 128) {
                break;
            }
        }

        return i;
    }

    public static int readVarInt(ByteArrayInputStream in) throws IOException {
        int i = 0;
        int j = 0;
        while (true) {
            int k = in.read();

            i |= (k & 0x7F) << j++ * 7;

            if (j > 5) {
                throw new RuntimeException("VarInt too big");
            }

            if ((k & 0x80) != 128) {
                break;
            }
        }

        return i;
    }

    public static int[] readVarIntt(DataInputStream in) throws IOException{ //reads a varint from the stream, returning both the length and the value
        int i = 0;
        int j = 0;
        int b = 0;
        while (true){
            int k = in.read();
            b += 1;
            i |= (k & 0x7F) << j++ * 7;

            if (j > 5) throw new RuntimeException("VarInt too big");

            if ((k & 0x80) != 128) break;
        }

        return new int[]{i,b};
    }

    public static int[] readVarIntt(ByteArrayInputStream in) { //reads a varint from the stream, returning both the length and the value
        int i = 0;
        int j = 0;
        int b = 0;
        while (true){
            int k = in.read();
            b += 1;
            i |= (k & 0x7F) << j++ * 7;

            if (j > 5) throw new RuntimeException("VarInt too big");

            if ((k & 0x80) != 128) break;
        }

        return new int[]{i,b};
    }
}
