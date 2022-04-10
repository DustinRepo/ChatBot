package me.dustin.chatbot.network.packet.c2s.play;

import me.dustin.chatbot.ChatBot;
import me.dustin.chatbot.network.Protocols;
import me.dustin.chatbot.network.packet.Packet;
import me.dustin.chatbot.network.packet.PacketIDs;
import me.dustin.chatbot.network.packet.pipeline.PacketByteBuf;

import java.io.IOException;

public class ServerBoundPlayerSwingPacket extends Packet {

    public static final int MAIN_HAND = 0, OFF_HAND = 1;

    private final int hand;

    public ServerBoundPlayerSwingPacket(int hand) {
        super(PacketIDs.ServerBound.PLAYER_SWING.getPacketId());
        this.hand = hand;
    }

    @Override
    public void createPacket(PacketByteBuf packetByteBuf) throws IOException {
        if (Protocols.getCurrent().getProtocolVer() > Protocols.V1_8.getProtocolVer())
            packetByteBuf.writeVarInt(hand);
    }

}
