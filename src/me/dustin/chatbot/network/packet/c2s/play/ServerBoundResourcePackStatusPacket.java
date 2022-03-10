package me.dustin.chatbot.network.packet.c2s.play;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import me.dustin.chatbot.network.packet.Packet;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class ServerBoundResourcePackStatusPacket extends Packet {

    public static final int SUCCESSFULLY_LOADED = 0, DECLINED = 1, FAILED_DL = 2, ACCEPTED = 3;

    private int result;

    public ServerBoundResourcePackStatusPacket(int result) {
        this.result = result;
    }

    @Override
    public ByteArrayDataOutput createPacket() throws IOException {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream packet = new DataOutputStream(baos);

        packet.write(0x21);//packet id
        writeVarInt(packet, result);

        writeVarInt(out, baos.toByteArray().length);
        out.write(baos.toByteArray());
        return out;
    }
}
