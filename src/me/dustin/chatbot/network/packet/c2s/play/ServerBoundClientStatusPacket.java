package me.dustin.chatbot.network.packet.c2s.play;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import me.dustin.chatbot.network.packet.Packet;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class ServerBoundClientStatusPacket extends Packet {
    public static final int RESPAWN = 0, REQUEST_STATS = 1;
    private int action;
    public ServerBoundClientStatusPacket(int action) {
        super(0x04);
        this.action = action;
    }

    @Override
    public ByteArrayDataOutput createPacket() throws IOException {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream clientStatusPacket = new DataOutputStream(baos);

        writeVarInt(clientStatusPacket, packetId);
        writeVarInt(clientStatusPacket, action);

        writeVarInt(out, baos.toByteArray().length);
        out.write(baos.toByteArray());
        return out;
    }
}
