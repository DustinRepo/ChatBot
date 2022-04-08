package me.dustin.chatbot.network.packet.c2s.play;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import me.dustin.chatbot.ChatBot;
import me.dustin.chatbot.network.Protocols;
import me.dustin.chatbot.network.packet.Packet;
import me.dustin.chatbot.network.packet.PacketIDs;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class ServerBoundKeepAlivePacket extends Packet {

    private long id;

    public ServerBoundKeepAlivePacket(long id) {
        this.id = id;
    }

    @Override
    public ByteArrayDataOutput createPacket() throws IOException {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream packet = new DataOutputStream(baos);

        writeVarInt(packet, PacketIDs.ServerBound.KEEP_ALIVE.getPacketId());
        if (ChatBot.getConfig().getProtocolVersion() <= Protocols.V1_12_1.getProtocolVer())//in 1.12.1 and below keepalive id is an int, not a long
            writeVarInt(packet, (int) id);
        else
            packet.writeLong(id);

        writeVarInt(out, baos.toByteArray().length);
        out.write(baos.toByteArray());
        return out;
    }
}
