package me.dustin.chatbot.network.packet;

import me.dustin.chatbot.helper.GeneralHelper;
import me.dustin.chatbot.network.ClientConnection;
import me.dustin.chatbot.network.packet.s2c.login.*;
import me.dustin.chatbot.network.packet.s2c.play.*;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.SocketException;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

public class ClientBoundPacketListener {
    
    private ClientConnection clientConnection;
    
    public ClientBoundPacketListener(ClientConnection clientConnection) {
        this.clientConnection = clientConnection;
    }

    public void listen() {
        Packet.ClientBoundPacket packet = getPacket();
        if (packet != null) {
            packet.apply();
        }
    }

    public Packet.ClientBoundPacket getPacket() {
        Packet.ClientBoundPacket packet = null;
        try {
            ByteArrayInputStream byteArrayInputStream = getPacketData();
            if (byteArrayInputStream != null) {
                int packetId = Packet.readVarInt(byteArrayInputStream);
                switch (clientConnection.getNetworkState()) {
                    case LOGIN -> {
                        switch (packetId) {
                            case 0x00 -> {
                                packet = new ClientBoundDisconnectPacket(clientConnection.getClientBoundPacketHandler());
                                packet.createPacket(byteArrayInputStream);
                            }
                            case 0x01 -> {
                                packet = new ClientBoundEncryptionStartPacket(clientConnection.getClientBoundPacketHandler());
                                packet.createPacket(byteArrayInputStream);
                            }
                            case 0x02 -> {
                                packet = new ClientBoundLoginSuccessPacket(clientConnection.getClientBoundPacketHandler());
                                packet.createPacket(byteArrayInputStream);
                            }
                            case 0x03 -> {
                                packet = new ClientBoundSetCompressionPacket(clientConnection.getClientBoundPacketHandler());
                                packet.createPacket(byteArrayInputStream);
                            }
                            case 0x04 -> {
                                packet = new ClientBoundPluginRequestPacket(clientConnection.getClientBoundPacketHandler());
                                packet.createPacket(byteArrayInputStream);
                            }
                            default -> {
                                GeneralHelper.print("Incorrect packet ID: " + packetId + " during LOGIN state", GeneralHelper.ANSI_RED);
                                clientConnection.close();
                            }
                        }
                    }
                    case PLAY -> {
                        switch (packetId) {
                            case 0x21 -> {
                                packet = new ClientBoundKeepAlivePacket(clientConnection.getClientBoundPacketHandler());
                                packet.createPacket(byteArrayInputStream);
                            }
                            case 0x1A -> {
                                packet = new ClientBoundDisconnectPlayPacket(clientConnection.getClientBoundPacketHandler());
                                packet.createPacket(byteArrayInputStream);
                            }
                            case 0x0F -> {
                                packet = new ClientBoundChatMessagePacket(clientConnection.getClientBoundPacketHandler());
                                packet.createPacket(byteArrayInputStream);
                            }
                            case 0x52 -> {
                                packet = new ClientBoundUpdateHealthPacket(clientConnection.getClientBoundPacketHandler());
                                packet.createPacket(byteArrayInputStream);
                            }
                            case 0x59 -> {
                                packet = new ClientBoundWorldTimePacket(clientConnection.getClientBoundPacketHandler());
                                packet.createPacket(byteArrayInputStream);
                            }
                            case 0x35 -> {
                                packet = new ClientBoundPlayerDeadPacket(clientConnection.getClientBoundPacketHandler());
                                packet.createPacket(byteArrayInputStream);
                            }
                            case 0x36 -> {
                                //TODO: actually set this packet up properly. seems confusing
                                /*packet = new ClientBoundPlayerInfoPacket(clientConnection.getClientBoundPacketHandler());
                                packet.createPacket(byteArrayInputStream);*/
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            if (e instanceof SocketException || e instanceof EOFException)
                clientConnection.close();
            else
                e.printStackTrace();
        }
        return packet;
    }

    public ByteArrayInputStream getPacketData() throws IOException {
        DataInputStream dataInputStream = clientConnection.getIn();
        if (clientConnection.getCompressionThreshold() > 0) {
            int length = Packet.readVarInt(dataInputStream);
            int[] dataLengths = Packet.readVarIntt(dataInputStream);
            int dataLength = dataLengths[0];
            int packetLegth = length-dataLengths[1];
            if (dataLength != 0) {
                readCompressed(packetLegth, dataLength);
            } else {
                return readUncompressed(packetLegth);
            }
        } else {
            return readUncompressed();
        }
        return null;
    }

    private ByteArrayInputStream readCompressed(int packetLength, int dataLength) throws IOException {
        DataInputStream dataInputStream = clientConnection.getIn();
        if (dataLength >= clientConnection.getCompressionThreshold()) {
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
        DataInputStream dataInputStream = clientConnection.getIn();
        int size = Packet.readVarInt(dataInputStream);
        byte[] readableBytes = new byte[size];
        dataInputStream.readFully(readableBytes);
        return new ByteArrayInputStream(readableBytes);
    }

    private ByteArrayInputStream readUncompressed(int size) throws IOException {
        DataInputStream dataInputStream = clientConnection.getIn();
        byte[] readableBytes = new byte[size];
        dataInputStream.readFully(readableBytes);
        return new ByteArrayInputStream(readableBytes);
    }

}
