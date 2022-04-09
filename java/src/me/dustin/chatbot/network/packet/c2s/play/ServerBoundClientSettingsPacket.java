package me.dustin.chatbot.network.packet.c2s.play;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import me.dustin.chatbot.ChatBot;
import me.dustin.chatbot.network.Protocols;
import me.dustin.chatbot.network.packet.Packet;
import me.dustin.chatbot.network.packet.PacketIDs;
import me.dustin.chatbot.network.packet.pipeline.PacketByteBuf;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class ServerBoundClientSettingsPacket extends Packet {

    private final String locale;
    private final boolean allowServerListings;
    private final int enabledSkinParts;

    public ServerBoundClientSettingsPacket(String locale, boolean allowServerListings, int enabledSkinParts) {
        super(PacketIDs.ServerBound.CLIENT_SETTINGS.getPacketId());
        this.locale = locale;
        this.allowServerListings = allowServerListings;
        this.enabledSkinParts = enabledSkinParts;
    }

    @Override
    public void createPacket(PacketByteBuf packetByteBuf) throws IOException {
        if (ChatBot.getConfig().getProtocolVersion() <= Protocols.V1_7_10.getProtocolVer()) {//packet is very different in 1.7.10 and below
            packetByteBuf.writeString(locale);
            packetByteBuf.writeByte(8);//render distance
            packetByteBuf.writeByte(0);//chat mode. 0 = enabled
            packetByteBuf.writeBoolean(true);//color chat
            packetByteBuf.writeByte(3);//difficulty
            packetByteBuf.writeBoolean(true);//show cape
        } else {
            packetByteBuf.writeString(locale);
            packetByteBuf.writeByte(8);//render distance
            packetByteBuf.writeVarInt(0);//chat mode. 0 = enabled
            packetByteBuf.writeBoolean(true);//chat colors
            packetByteBuf.writeByte(enabledSkinParts);
            if (ChatBot.getConfig().getProtocolVersion() > Protocols.V1_8.getProtocolVer())//1.8 and below didn't have main/offhand
                packetByteBuf.writeVarInt(1);//main hand - 0 = left 1 = right
            if (ChatBot.getConfig().getProtocolVersion() >= Protocols.V1_17.getProtocolVer())
                packetByteBuf.writeBoolean(false);//text filtering
            if (ChatBot.getConfig().getProtocolVersion() >= Protocols.V1_18.getProtocolVer())//1.18, I *think* the version this was added
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
