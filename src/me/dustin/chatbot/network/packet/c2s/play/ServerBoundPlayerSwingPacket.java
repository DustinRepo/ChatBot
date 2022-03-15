package me.dustin.chatbot.network.packet.c2s.play;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import me.dustin.chatbot.ChatBot;
import me.dustin.chatbot.helper.Protocols;
import me.dustin.chatbot.network.packet.Packet;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class ServerBoundPlayerSwingPacket extends Packet {

    public static final int MAIN_HAND = 0, OFF_HAND = 1;

    private final int hand;

    public ServerBoundPlayerSwingPacket(int hand) {
        this.hand = hand;
    }

    @Override
    public ByteArrayDataOutput createPacket() throws IOException {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dataOutputStream = new DataOutputStream(baos);
        int packetId = 0x2C;
        if (ChatBot.getConfig().getProtocolVersion() <= Protocols.V1_16_1.getProtocolVer())
            packetId = 0x2b;
        if (ChatBot.getConfig().getProtocolVersion() <= Protocols.V1_15_2.getProtocolVer())
            packetId = 0x2a;
        if (ChatBot.getConfig().getProtocolVersion() <= Protocols.V1_13_2.getProtocolVer())
            packetId = 0x27;
        if (ChatBot.getConfig().getProtocolVersion() <= Protocols.V1_12_2.getProtocolVer())
            packetId = 0x1D;
        dataOutputStream.write(packetId);//packet id
        writeVarInt(dataOutputStream, hand);

        writeVarInt(out, baos.toByteArray().length);
        out.write(baos.toByteArray());
        return out;
    }

}
