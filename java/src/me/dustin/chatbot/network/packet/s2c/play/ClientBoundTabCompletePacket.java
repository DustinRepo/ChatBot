package me.dustin.chatbot.network.packet.s2c.play;

import me.dustin.chatbot.ChatBot;
import me.dustin.chatbot.network.Protocols;
import me.dustin.chatbot.network.packet.PacketIDs;
import me.dustin.chatbot.network.packet.pipeline.PacketByteBuf;
import me.dustin.chatbot.network.packet.Packet;
import me.dustin.chatbot.network.packet.handler.ClientBoundPacketHandler;
import me.dustin.chatbot.network.packet.handler.ClientBoundPlayClientBoundPacketHandler;

import java.io.IOException;
import java.util.ArrayList;

public class ClientBoundTabCompletePacket extends Packet.ClientBoundPacket {

    private int id;
    private int start;
    private int length;
    private final ArrayList<TabCompleteMatch> matches = new ArrayList<>();

    public ClientBoundTabCompletePacket(ClientBoundPacketHandler clientBoundPacketHandler) {
        super(PacketIDs.ClientBound.TAB_COMPLETE.getPacketId(), clientBoundPacketHandler);
    }

    @Override
    public void createPacket(PacketByteBuf packetByteBuf) throws IOException {
        if (ChatBot.getConfig().getProtocolVersion() <= Protocols.V1_12_2.getProtocolVer()) {//1.12
            int size = packetByteBuf.readVarInt();
            for (int i = 0; i < size - 1; i++) {
                this.matches.add(new TabCompleteMatch(packetByteBuf.readString(), false, ""));
            }
            return;
        }
        this.id = packetByteBuf.readVarInt();
        this.start = packetByteBuf.readVarInt();
        this.length = packetByteBuf.readVarInt();
        int arraylength = packetByteBuf.readVarInt();
        for (int i = 0; i < arraylength; i++) {
            String m = packetByteBuf.readString();
            boolean tt = packetByteBuf.readBoolean();
            String s = "";
            if (tt)
                s = packetByteBuf.readString();
            this.matches.add(new TabCompleteMatch(m, tt, s));
        }
    }

    @Override
    public void apply() {
        ((ClientBoundPlayClientBoundPacketHandler)clientBoundPacketHandler).handleTabComplete(this);
    }

    public int getId() {
        return id;
    }

    public int getStart() {
        return start;
    }

    public int getLength() {
        return length;
    }

    public ArrayList<TabCompleteMatch> getMatches() {
        return matches;
    }

    public record TabCompleteMatch(String match, boolean hasTooltip, String tooltip) {}
}
