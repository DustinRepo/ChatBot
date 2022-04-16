package me.dustin.chatbot.network.packet.impl.play.c2s;

import me.dustin.chatbot.network.packet.Packet;
import me.dustin.chatbot.network.ProtocolHandler;
import me.dustin.chatbot.network.packet.pipeline.PacketByteBuf;

import java.io.IOException;

public class ServerBoundPlayerSwingPacket extends Packet {

    public static final int MAIN_HAND = 0, OFF_HAND = 1;

    private final int hand;

    public ServerBoundPlayerSwingPacket(int hand) {
        super(ProtocolHandler.getCurrent().getPacketId(ProtocolHandler.NetworkSide.SERVERBOUND, "swing_arm"));
        this.hand = hand;
    }

    @Override
    public void createPacket(PacketByteBuf packetByteBuf) throws IOException {
        if (ProtocolHandler.getCurrent().getProtocolVer() > ProtocolHandler.getVersionFromName("1.8.9").getProtocolVer())
            packetByteBuf.writeVarInt(hand);
        if (ProtocolHandler.getCurrent().getProtocolVer() <= ProtocolHandler.getVersionFromName("1.7.10").getProtocolVer()) {
            packetByteBuf.writeInt(getClientConnection().getClientPlayer().getEntityId());
            packetByteBuf.writeByte(1);
        }
    }

}
