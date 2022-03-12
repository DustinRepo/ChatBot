package me.dustin.chatbot.network.packet.handler;

import me.dustin.chatbot.network.ClientConnection;
import me.dustin.chatbot.network.packet.Packet;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

public abstract class ClientBoundPacketHandler {

    private final ClientConnection clientConnection;

    private final Map<Integer, Class<? extends Packet.ClientBoundPacket>> packetMap = new HashMap<>();

    public ClientBoundPacketHandler(ClientConnection clientConnection) {
        this.clientConnection = clientConnection;
    }

    public void listen() {
        try {
            ByteArrayInputStream packetData = getPacketData();
            if (packetData != null) {
                int packetId = Packet.readVarInt(packetData);
                Class<? extends Packet.ClientBoundPacket> c = packetMap.get(packetId);
                if (c != null) {
                    Packet.ClientBoundPacket packet = c.getDeclaredConstructor(ClientBoundPacketHandler.class).newInstance(this);
                    packet.setClientConnection(getClientConnection());
                    packet.createPacket(packetData);
                    packet.apply();
                }
            }
        } catch (Exception e) {
            getClientConnection().close();
            e.printStackTrace();
        }
    }

    protected Map<Integer, Class<? extends Packet.ClientBoundPacket>> getPacketMap() {
        return packetMap;
    }

    protected ClientConnection getClientConnection() {
        return clientConnection;
    }

    public ByteArrayInputStream getPacketData() throws IOException {
        DataInputStream dataInputStream = getClientConnection().getIn();
        if (getClientConnection().getCompressionThreshold() > 0) {
            int length = Packet.readVarInt(dataInputStream);
            int[] dataLengths = Packet.readVarIntt(dataInputStream);
            int dataLength = dataLengths[0];
            int packetLegth = length-dataLengths[1];
            if (dataLength != 0) {
                return readCompressed(packetLegth, dataLength);
            } else {
                return readUncompressed(packetLegth);
            }
        } else {
            return readUncompressed();
        }
    }

    private ByteArrayInputStream readCompressed(int packetLength, int dataLength) throws IOException {
        DataInputStream dataInputStream = getClientConnection().getIn();
        if (dataLength >= getClientConnection().getCompressionThreshold()) {
            byte[] data = new byte[packetLength];
            dataInputStream.readFully(data, 0, packetLength);

            Inflater inflater = new Inflater();
            inflater.setInput(data);

            byte[] uncompressed = new byte[dataLength];
            try {
                inflater.inflate(uncompressed);
            } catch (DataFormatException dataFormatException) {
                dataFormatException.printStackTrace();
                throw new IOException("Bad packet. DataFormatException");
            } finally {
                inflater.end();
            }
            return new ByteArrayInputStream(uncompressed);
        } else
            throw new IOException("Bad packet. dataLength was smaller than compression threshhold");
    }

    private ByteArrayInputStream readUncompressed() throws IOException {
        DataInputStream dataInputStream = getClientConnection().getIn();
        int size = Packet.readVarInt(dataInputStream);
        byte[] readableBytes = new byte[size];
        dataInputStream.readFully(readableBytes);
        return new ByteArrayInputStream(readableBytes);
    }

    private ByteArrayInputStream readUncompressed(int size) throws IOException {
        DataInputStream dataInputStream = getClientConnection().getIn();
        byte[] readableBytes = new byte[size];
        dataInputStream.readFully(readableBytes);
        return new ByteArrayInputStream(readableBytes);
    }
}
