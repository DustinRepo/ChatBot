package me.dustin.chatbot.network.packet.c2s.play;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import me.dustin.chatbot.ChatBot;
import me.dustin.chatbot.network.packet.Packet;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class ServerBoundChatPacket extends Packet {
    String message;
    public ServerBoundChatPacket(String message) {
        if (message.length() > 256) {
            message = message.substring(0, 256);
        }
        this.message = message;
    }

    @Override
    public ByteArrayDataOutput createPacket() throws IOException {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dataOutputStream = new DataOutputStream(baos);

        int packetId = 0x03;
        if (ChatBot.getConfig().getProtocolVersion() <= 404)//1.13.2
            packetId = 0x02;

        writeVarInt(dataOutputStream, packetId);
        writeString(dataOutputStream, message);

        writeVarInt(out, baos.toByteArray().length);
        out.write(baos.toByteArray());
        return out;
    }
}
