package me.dustin.chatbot.network.packet.c2s.handshake;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import me.dustin.chatbot.network.packet.Packet;
import me.dustin.chatbot.network.packet.pipeline.PacketByteBuf;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class ServerBoundHandshakePacket extends Packet {

    public static final int HANDSHAKING_STATE = -1;
    public static final int PLAY_STATE = 0;
    public static final int STATUS_STATE = 1;
    public static final int LOGIN_STATE = 2;

    public int networkState;
    public String serverIP;
    public int protocolVersion, serverPort;

    public ServerBoundHandshakePacket(int protocolVersion, String serverIP, int serverPort, int networkState) {
        super(0x00);
        this.networkState = networkState;
        this.protocolVersion = protocolVersion;
        this.serverIP = serverIP;
        this.serverPort = serverPort;
    }

    @Override
    public void createPacket(PacketByteBuf packetByteBuf) throws IOException {
        packetByteBuf.writeVarInt(protocolVersion);
        packetByteBuf.writeString(serverIP);
        packetByteBuf.writeShort(serverPort);
        packetByteBuf.writeVarInt(networkState);
    }

}