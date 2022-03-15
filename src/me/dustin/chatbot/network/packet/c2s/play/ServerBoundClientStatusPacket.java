package me.dustin.chatbot.network.packet.c2s.play;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import me.dustin.chatbot.ChatBot;
import me.dustin.chatbot.helper.Protocols;
import me.dustin.chatbot.network.packet.Packet;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class ServerBoundClientStatusPacket extends Packet {
    public static final int RESPAWN = 0, REQUEST_STATS = 1;
    private final int action;
    public ServerBoundClientStatusPacket(int action) {
        this.action = action;
    }

    @Override
    public ByteArrayDataOutput createPacket() throws IOException {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream clientStatusPacket = new DataOutputStream(baos);

        int packetId = 0x04;
        if (ChatBot.getConfig().getProtocolVersion() <= Protocols.V1_13_2.getProtocolVer() && ChatBot.getConfig().getProtocolVersion() != Protocols.V1_12.getProtocolVer())
            packetId = 0x03;

        writeVarInt(clientStatusPacket, packetId);
        writeVarInt(clientStatusPacket, action);

        writeVarInt(out, baos.toByteArray().length);
        out.write(baos.toByteArray());
        return out;
    }
}
