package me.dustin.chatbot.network.packet.impl.play.c2s;

import me.dustin.chatbot.network.packet.Packet;
import me.dustin.chatbot.network.packet.ProtocolHandler;
import me.dustin.chatbot.network.packet.pipeline.PacketByteBuf;

import java.io.IOException;

public class ServerBoundResourcePackStatusPacket extends Packet {

    public static final int SUCCESSFULLY_LOADED = 0, DECLINED = 1, FAILED_DL = 2, ACCEPTED = 3;

    private final int result;

    public ServerBoundResourcePackStatusPacket(int result) {
        super(ProtocolHandler.getCurrent().getPacketId(ProtocolHandler.NetworkSide.SERVERBOUND, "resourcepack"));
        this.result = result;
    }

    @Override
    public void createPacket(PacketByteBuf packetByteBuf) throws IOException {
        packetByteBuf.writeVarInt(result);
    }
}
