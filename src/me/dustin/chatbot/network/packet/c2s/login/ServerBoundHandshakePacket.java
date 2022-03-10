package me.dustin.chatbot.network.packet.c2s.login;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import me.dustin.chatbot.network.packet.Packet;

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
    public ByteArrayDataOutput createPacket() throws IOException {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        ByteArrayOutputStream handshakeBytes = new ByteArrayOutputStream();
        DataOutputStream handshakePacket = new DataOutputStream(handshakeBytes);

        //write packetID with the data so we can just easily get their size together
        handshakePacket.writeByte(this.packetId);
        writeVarInt(handshakePacket, protocolVersion);
        writeString(handshakePacket, serverIP);
        handshakePacket.writeShort(serverPort);
        writeVarInt(handshakePacket, networkState);

        //send size of data and the data
        writeVarInt(out, handshakeBytes.size());
        out.write(handshakeBytes.toByteArray());

        handshakeBytes.close();
        handshakePacket.close();
        return out;
    }

}
