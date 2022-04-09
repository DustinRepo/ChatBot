package me.dustin.chatbot.network.packet.c2s.login;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import me.dustin.chatbot.network.packet.Packet;
import me.dustin.chatbot.network.packet.pipeline.PacketByteBuf;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class ServerBoundPluginResponsePacket extends Packet {
    private final int messageId;
    public ServerBoundPluginResponsePacket(int messageId) {
        super(0x02);
        this.messageId = messageId;
    }

    @Override
    public void createPacket(PacketByteBuf packetByteBuf) throws IOException {
        byte[] bytes = new byte[0];
        packetByteBuf.writeVarInt(messageId);//id from request packet
        packetByteBuf.writeBoolean(false);//tell the server we don't give a shit
        packetByteBuf.writeBytes(bytes);//empty byte array
    }
}
