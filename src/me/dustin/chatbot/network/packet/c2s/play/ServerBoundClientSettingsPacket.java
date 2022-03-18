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

public class ServerBoundClientSettingsPacket extends Packet {

    private final String locale;
    private final boolean allowServerListings;
    private final int enabledSkinParts;

    public ServerBoundClientSettingsPacket(String locale, boolean allowServerListings, int enabledSkinParts) {
        this.locale = locale;
        this.allowServerListings = allowServerListings;
        this.enabledSkinParts = enabledSkinParts;
    }

    @Override
    public ByteArrayDataOutput createPacket() throws IOException {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream packet = new DataOutputStream(baos);

        writeVarInt(packet, PacketIDs.ServerBound.CLIENT_SETTINGS.getPacketId());//packet id
        writeString(packet, locale);
        packet.writeByte(8);//render distance
        writeVarInt(packet, 0);//chat mode. 0 = enabled
        packet.writeBoolean(true);//chat colors
        packet.writeByte(enabledSkinParts);
        if (ChatBot.getConfig().getProtocolVersion() > Protocols.V1_8.getProtocolVer())//1.8 and below didn't have main/offhand
            writeVarInt(packet, 1);//main hand - 0 = left 1 = right
        if (ChatBot.getConfig().getProtocolVersion() >= Protocols.V1_17.getProtocolVer())
            packet.writeBoolean(false);//text filtering
        if (ChatBot.getConfig().getProtocolVersion() >= Protocols.V1_18.getProtocolVer())//1.18, I *think* the version this was added
            packet.writeBoolean(allowServerListings);

        writeVarInt(out, baos.toByteArray().length);
        out.write(baos.toByteArray());
        return out;
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
