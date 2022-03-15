package me.dustin.chatbot.network.packet.c2s.play;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import me.dustin.chatbot.ChatBot;
import me.dustin.chatbot.network.packet.Packet;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class ServerBoundPlayerRotationPacket extends Packet {

    private final float yaw, pitch;
    private final boolean onGround;

    public ServerBoundPlayerRotationPacket(float yaw, float pitch, boolean onGround) {
        this.yaw = yaw;
        this.pitch = pitch;
        this.onGround = onGround;
    }

    @Override
    public ByteArrayDataOutput createPacket() throws IOException {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dataOutputStream = new DataOutputStream(baos);
        int packetId = 0x13;
        if (ChatBot.getConfig().getProtocolVersion() <= 754 && ChatBot.getConfig().getProtocolVersion() > 578)
            packetId = 0x14;
        if (ChatBot.getConfig().getProtocolVersion() <= 404)
            packetId = 0x12;
        if (ChatBot.getConfig().getProtocolVersion() <= 340)
            packetId = 0xF;
        dataOutputStream.write(packetId);//packet id
        dataOutputStream.writeFloat(yaw);
        dataOutputStream.writeFloat(pitch);
        dataOutputStream.writeBoolean(onGround);

        writeVarInt(out, baos.toByteArray().length);
        out.write(baos.toByteArray());
        return out;
    }
}
