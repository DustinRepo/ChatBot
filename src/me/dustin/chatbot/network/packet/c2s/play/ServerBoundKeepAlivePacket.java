package me.dustin.chatbot.network.packet.c2s.play;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import me.dustin.chatbot.network.packet.Packet;

import java.io.IOException;

public class ServerBoundKeepAlivePacket extends Packet {

    private long id;

    public ServerBoundKeepAlivePacket(long id) {
        super(0x0F);
        this.id = id;
    }

    @Override
    public ByteArrayDataOutput createPacket() throws IOException {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeByte(0x09);
        out.writeByte(this.packetId);
        out.writeLong(id);
        return out;
    }
}
