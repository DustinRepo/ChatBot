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

        System.out.println(PacketIDs.ServerBound.TAB_COMPLETE.getPacketId());

        writeVarInt(packet, PacketIDs.ServerBound.TAB_COMPLETE.getPacketId());//packet id

        if (ChatBot.getConfig().getProtocolVersion() <= Protocols.V1_12_2.getProtocolVer()) {
            writeString(packet, cmd);//text
            packet.writeBoolean(false);//assume command - used for cmd blocks
            packet.writeBoolean(false);//has position
        } else {
            writeVarInt(packet, transactionId);
            writeString(packet, cmd);
        }
        writeVarInt(out, baos.toByteArray().length);
        out.write(baos.toByteArray());
        return out;
    }
}
