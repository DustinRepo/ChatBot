package me.dustin.chatbot.network.packet.s2c.play;

import me.dustin.chatbot.ChatBot;
import me.dustin.chatbot.network.packet.Packet;
import me.dustin.chatbot.network.packet.handler.ClientBoundPacketHandler;
import me.dustin.chatbot.network.packet.handler.ClientBoundPlayClientBoundPacketHandler;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;

public class ClientBoundTabCompletePacket extends Packet.ClientBoundPacket {

    private int id;
    private int start;
    private int length;
    private ArrayList<TabCompleteMatch> matches = new ArrayList<>();

    public ClientBoundTabCompletePacket(ClientBoundPacketHandler clientBoundPacketHandler) {
        super(clientBoundPacketHandler);
    }

    @Override
    public void createPacket(ByteArrayInputStream byteArrayInputStream) throws IOException {
        DataInputStream dataInputStream = new DataInputStream(byteArrayInputStream);
        if (ChatBot.getConfig().getProtocolVersion() == 340) {//1.12
            int size = readVarInt(dataInputStream);
            for (int i = 0; i < size - 1; i++) {
                matches.add(new TabCompleteMatch(readString(dataInputStream), false, ""));
            }
            return;
        }
        id = readVarInt(dataInputStream);
        start = readVarInt(dataInputStream);
        length = readVarInt(dataInputStream);
        int arraylength = readVarInt(dataInputStream);
        for (int i = 0; i < arraylength; i++) {
            String m = readString(dataInputStream);
            boolean tt = dataInputStream.readBoolean();
            String s = "";
            if (tt)
                s = readString(dataInputStream);
            matches.add(new TabCompleteMatch(m, tt, s));
        }
        super.createPacket(byteArrayInputStream);
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
