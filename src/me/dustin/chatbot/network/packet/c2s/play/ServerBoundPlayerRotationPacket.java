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

        dataOutputStream.write(PacketIDs.ServerBound.PLAYER_ROTATION.getPacketId());//packet id
        dataOutputStream.writeFloat(yaw);
        dataOutputStream.writeFloat(pitch);
        dataOutputStream.writeBoolean(onGround);

        writeVarInt(out, baos.toByteArray().length);
        out.write(baos.toByteArray());
        return out;
    }
}
