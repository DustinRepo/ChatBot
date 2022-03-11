package me.dustin.chatbot.network.packet.c2s.play;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import me.dustin.chatbot.network.packet.Packet;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class ServerBoundClientSettingsPacket extends Packet {

    private final String locale;
    private final boolean allowServerListings;

    public ServerBoundClientSettingsPacket(String locale, boolean allowServerListings) {
        this.locale = locale;
        this.allowServerListings = allowServerListings;
    }

    @Override
    public ByteArrayDataOutput createPacket() throws IOException {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream packet = new DataOutputStream(baos);

        //displayed skin parts. just enable all of them
        int skinParts = 0;
        skinParts |= 0x01;//cape
        skinParts |= 0x02;//jacket
        skinParts |= 0x04;//left sleeve
        skinParts |= 0x08;//right sleeve
        skinParts |= 0x10;//left pants
        skinParts |= 0x20;//right pants
        skinParts |= 0x40;//hat

        writeVarInt(packet, 0x05);//packet id
        writeString(packet, locale);
        packet.writeByte(8);//render distance
        writeVarInt(packet, 0);//chat mode. 0 = enabled
        packet.writeBoolean(true);//chat colors
        packet.writeByte(skinParts);
        writeVarInt(packet, 1);//main hand - 0 = left 1 = right
        packet.writeBoolean(false);//text filtering
        packet.writeBoolean(allowServerListings);

        writeVarInt(out, baos.toByteArray().length);
        out.write(baos.toByteArray());
        return out;
    }
}
