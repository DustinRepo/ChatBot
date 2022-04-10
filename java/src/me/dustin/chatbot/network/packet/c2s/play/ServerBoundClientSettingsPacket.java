package me.dustin.chatbot.network.packet.c2s.play;

import me.dustin.chatbot.network.packet.Packet;
import me.dustin.chatbot.network.packet.ProtocolHandler;
import me.dustin.chatbot.network.packet.pipeline.PacketByteBuf;

import java.io.IOException;

public class ServerBoundClientSettingsPacket extends Packet {

    private final String locale;
    private final boolean allowServerListings;
    private final int enabledSkinParts;

    public ServerBoundClientSettingsPacket(String locale, boolean allowServerListings, int enabledSkinParts) {
        super(ProtocolHandler.getCurrent().getPacketId(ProtocolHandler.NetworkSide.SERVERBOUND, "settings"));
        this.locale = locale;
        this.allowServerListings = allowServerListings;
        this.enabledSkinParts = enabledSkinParts;
    }

    @Override
    public void createPacket(PacketByteBuf packetByteBuf) throws IOException {
        if (ProtocolHandler.getCurrent().getProtocolVer() <= ProtocolHandler.getVersionFromName("1.7.10").getProtocolVer()) {//packet is very different in 1.7.10 and below
            packetByteBuf.writeString(locale);
            packetByteBuf.writeByte(8);//render distance
            packetByteBuf.writeByte(0);//chat mode. 0 = enabledcons
            packetByteBuf.writeBoolean(true);//color chat
            packetByteBuf.writeByte(3);//difficulty
            packetByteBuf.writeBoolean(true);//show cape
        } else if (ProtocolHandler.getCurrent().getProtocolVer() <= ProtocolHandler.getVersionFromName("1.8.9").getProtocolVer()) {
            packetByteBuf.writeString(locale);
            packetByteBuf.writeByte(8);//render distance
            packetByteBuf.writeByte(0);//chat mode. 0 = enabled
            packetByteBuf.writeBoolean(true);//color chat
            packetByteBuf.writeByte(3);//difficulty
        } else {
            packetByteBuf.writeString(locale);
            packetByteBuf.writeByte(8);//render distance
            packetByteBuf.writeVarInt(0);//chat mode. 0 = enabled
            packetByteBuf.writeBoolean(true);//chat colors
            packetByteBuf.writeByte(enabledSkinParts);
            packetByteBuf.writeVarInt(1);//main hand - 0 = left 1 = right
            if (ProtocolHandler.getCurrent().getProtocolVer() >= ProtocolHandler.getVersionFromName("1.17").getProtocolVer())
                packetByteBuf.writeBoolean(false);//text filtering
            if (ProtocolHandler.getCurrent().getProtocolVer() >= ProtocolHandler.getVersionFromName("1.18.1").getProtocolVer())
                packetByteBuf.writeBoolean(allowServerListings);
        }
    }

    public enum SkinPart {
        CAPE(0), JACKET(1), LEFT_SLEEVE(2), RIGHT_SLEEVE(3), LEFT_PANTS(4), RIGHT_PANTS(5), HAT(6);
        private final int bitFlag;
        SkinPart(int bitFlag) {
            this.bitFlag = 1 << bitFlag;
        }

        public static int all() {
            int all = 0;
            for (SkinPart playerModelPart : SkinPart.values()) {
                all |= playerModelPart.getBitFlag();
            }
            return all;
        }

        public int getBitFlag() {
            return bitFlag;
        }
    }
}
