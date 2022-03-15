package me.dustin.chatbot.network.packet.handler;

import me.dustin.chatbot.helper.GeneralHelper;
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
            DataInputStream packetData = getPacketData();
            if (packetData != null && packetData.available() > 0 && packetData.available() <= 2097050) {//max incoming packet size
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

    public DataInputStream getPacketData() throws IOException {
        DataInputStream dataInputStream = getClientConnection().getIn();
        if (getClientConnection().getCompressionThreshold() > 0) {
            int length = Packet.readVarInt(dataInputStream);
            int[] dataLengths = Packet.readVarIntt(dataInputStream);
            int dataLength = dataLengths[0];
            int packetLegth = length-dataLengths[1];
            if (dataLength != 0) {
                return readCompressed(dataInputStream, packetLegth, dataLength);
            } else {
                byte[] readableBytes = new byte[packetLegth];
                dataInputStream.readFully(readableBytes);
                return new DataInputStream(new ByteArrayInputStream(readableBytes));
            }
        } else {
            Packet.readVarInt(dataInputStream);//read size even tho we don't use it since we don't expect it later
            return dataInputStream;
        }
    }

    private DataInputStream readCompressed(DataInputStream dataInputStream, int packetLength, int dataLength) throws IOException {
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
            return new DataInputStream(new ByteArrayInputStream(uncompressed));
        } else
            throw new IOException("Bad packet. dataLength was smaller than compression threshhold");
    }
}
