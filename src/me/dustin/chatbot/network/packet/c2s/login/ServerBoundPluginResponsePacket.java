package me.dustin.chatbot.network.packet.c2s.login;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import me.dustin.chatbot.network.packet.Packet;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class ServerBoundPluginResponsePacket extends Packet {
    private final int messageId;
    public ServerBoundPluginResponsePacket(int messageId) {
        this.messageId = messageId;
    }

    @Override
    public ByteArrayDataOutput createPacket() throws IOException {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream pluginResponsePacket = new DataOutputStream(baos);
        byte[] bytes = new byte[0];

        writeVarInt(pluginResponsePacket, 0x02);//packet id
        writeVarInt(pluginResponsePacket, messageId);//id from request packet
        pluginResponsePacket.writeBoolean(false);//tell the server we don't give a shit
        pluginResponsePacket.write(bytes);//empty byte array

        out.write(baos.toByteArray().length);
        out.write(baos.toByteArray());
        return out;
    }
}
