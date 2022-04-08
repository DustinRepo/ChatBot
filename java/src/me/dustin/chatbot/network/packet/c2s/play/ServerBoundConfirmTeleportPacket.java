package me.dustin.chatbot.network.packet.c2s.play;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import me.dustin.chatbot.network.packet.Packet;
import me.dustin.chatbot.network.packet.PacketIDs;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class ServerBoundConfirmTeleportPacket extends Packet {

    private final int id;

    public ServerBoundConfirmTeleportPacket(int id) {
        this.id = id;
    }

    @Override
    public ByteArrayDataOutput createPacket() throws IOException {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream packet = new DataOutputStream(baos);

        writeVarInt(packet, PacketIDs.ServerBound.CONFIRM_TELEPORT.getPacketId());//packet id
        writeVarInt(packet, id);

        writeVarInt(out, baos.toByteArray().length);
        out.write(baos.toByteArray());
        return out;
    }
}
