package me.dustin.chatbot.network.packet.c2s.play;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import me.dustin.chatbot.ChatBot;
import me.dustin.chatbot.network.packet.Packet;

import java.io.IOException;

public class ServerBoundKeepAlivePacket extends Packet {

    private long id;

    public ServerBoundKeepAlivePacket(long id) {
        this.id = id;
    }

    @Override
    public ByteArrayDataOutput createPacket() throws IOException {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        int packetId = 0x0F;
        if (ChatBot.getConfig().getProtocolVersion() <= 754 && ChatBot.getConfig().getProtocolVersion() > 578)
            packetId = 0x10;
        if (ChatBot.getConfig().getProtocolVersion() <= 404)
            packetId = 0x0E;
        if (ChatBot.getConfig().getProtocolVersion() <= 340)
            packetId = 0xB;
        out.writeByte(0x09);
        out.writeByte(packetId);
        out.writeLong(id);
        return out;
    }
}
