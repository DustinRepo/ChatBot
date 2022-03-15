package me.dustin.chatbot.network.packet.c2s.play;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import me.dustin.chatbot.ChatBot;
import me.dustin.chatbot.network.packet.Packet;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class ServerBoundTabCompletePacket extends Packet {

    private final int transactionId;
    private final String cmd;

    public ServerBoundTabCompletePacket(int transactionId, String cmd) {
        this.transactionId = transactionId;
        this.cmd = cmd;
    }

    @Override
    public ByteArrayDataOutput createPacket() throws IOException {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream packet = new DataOutputStream(baos);

        int packetId = 0x06;
        if (ChatBot.getConfig().getProtocolVersion() <= 404)//1.13.2
            packetId = 0x05;
        if (ChatBot.getConfig().getProtocolVersion() <= 340)//1.12.2
            packetId = 0x01;

        writeVarInt(packet, packetId);//packet id
        writeVarInt(packet, transactionId);
        writeString(packet, cmd);

        writeVarInt(out, baos.toByteArray().length);
        out.write(baos.toByteArray());
        return out;
    }
}
