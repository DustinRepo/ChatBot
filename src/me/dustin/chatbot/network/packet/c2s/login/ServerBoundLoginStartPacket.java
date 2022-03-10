package me.dustin.chatbot.network.packet.c2s.login;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import me.dustin.chatbot.network.packet.Packet;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class ServerBoundLoginStartPacket extends Packet {
    private String name;
    public ServerBoundLoginStartPacket(String name) {
        this.name = name;
    }

    @Override
    public ByteArrayDataOutput createPacket() throws IOException {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        ByteArrayOutputStream loginBytes = new ByteArrayOutputStream();
        DataOutputStream loginPacket = new DataOutputStream(loginBytes);

        writeVarInt(loginPacket, 0x00);//packet id

        writeString(loginPacket, name);

        out.writeByte(loginBytes.size());//size of data + packet id
        out.write(loginBytes.toByteArray());

        loginBytes.close();
        loginPacket.close();

        return out;
    }
}
