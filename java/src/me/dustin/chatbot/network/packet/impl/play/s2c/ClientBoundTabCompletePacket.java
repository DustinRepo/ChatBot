package me.dustin.chatbot.network.packet.impl.play.s2c;

import me.dustin.chatbot.network.ProtocolHandler;
import me.dustin.chatbot.network.packet.pipeline.PacketByteBuf;
import me.dustin.chatbot.network.packet.Packet;
import me.dustin.chatbot.network.packet.handler.ClientBoundPacketHandler;
import me.dustin.chatbot.network.packet.handler.PlayClientBoundPacketHandler;

import java.util.ArrayList;

public class ClientBoundTabCompletePacket extends Packet.ClientBoundPacket {

    private final ArrayList<TabCompleteMatch> matches = new ArrayList<>();

    public ClientBoundTabCompletePacket(PacketByteBuf packetByteBuf) {
        super(packetByteBuf);
        if (ProtocolHandler.getCurrent().getProtocolVer() <= ProtocolHandler.getVersionFromName("1.12.2").getProtocolVer()) {//1.12
            int size = packetByteBuf.readVarInt();
            for (int i = 0; i < size - 1; i++) {
                this.matches.add(new TabCompleteMatch(packetByteBuf.readString(), false, ""));
            }

            return;
        }
        int id = packetByteBuf.readVarInt();
        int start = packetByteBuf.readVarInt();
        int length = packetByteBuf.readVarInt();
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
    public void handlePacket(ClientBoundPacketHandler clientBoundPacketHandler) {
        ((PlayClientBoundPacketHandler)clientBoundPacketHandler).handleTabComplete(this);
    }

    public ArrayList<TabCompleteMatch> getMatches() {
        return matches;
    }

    public record TabCompleteMatch(String match, boolean hasTooltip, String tooltip) {}
}
