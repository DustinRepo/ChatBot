package me.dustin.chatbot.network.packet.c2s.play;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import me.dustin.chatbot.ChatBot;
import me.dustin.chatbot.network.packet.Packet;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class ServerBoundResourcePackStatusPacket extends Packet {

    public static final int SUCCESSFULLY_LOADED = 0, DECLINED = 1, FAILED_DL = 2, ACCEPTED = 3;

    private final int result;

    public ServerBoundResourcePackStatusPacket(int result) {
        this.result = result;
    }

    @Override
    public ByteArrayDataOutput createPacket() throws IOException {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream packet = new DataOutputStream(baos);
        int packetId = 0x21;
        if (ChatBot.getConfig().getProtocolVersion() <= 736)
            packetId = 0x20;
        if (ChatBot.getConfig().getProtocolVersion() <= 578)
            packetId = 0x1F;
        if (ChatBot.getConfig().getProtocolVersion() <= 404)
            packetId = 0x1D;
        if (ChatBot.getConfig().getProtocolVersion() == 340)
            packetId = 0x18;
        packet.write(packetId);//packet id
        writeVarInt(packet, result);

        writeVarInt(out, baos.toByteArray().length);
        out.write(baos.toByteArray());
        return out;
    }
}
