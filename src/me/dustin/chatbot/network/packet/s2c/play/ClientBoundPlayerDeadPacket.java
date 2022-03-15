package me.dustin.chatbot.network.packet.s2c.play;

import me.dustin.chatbot.ChatBot;
import me.dustin.chatbot.network.packet.Packet;
import me.dustin.chatbot.network.packet.handler.ClientBoundPlayClientBoundPacketHandler;
import me.dustin.chatbot.network.packet.handler.ClientBoundPacketHandler;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

public class ClientBoundPlayerDeadPacket extends Packet.ClientBoundPacket {
    public static final int ENTER_COMBAT = 0, END_COMBAT = 1, ENTITY_DIED = 2;
    private int type = -1;//1.16.5 and below

    private int playerId;
    private int killerId;
    private String message = "";
    public ClientBoundPlayerDeadPacket(ClientBoundPacketHandler clientBoundPacketHandler) {
        super(clientBoundPacketHandler);
    }

    @Override
    public void createPacket(ByteArrayInputStream byteArrayInputStream) throws IOException {
        DataInputStream dataInputStream = new DataInputStream(byteArrayInputStream);
        if (ChatBot.getConfig().getProtocolVersion() <= 754) {//1.16.5
            this.type = readVarInt(dataInputStream);
            if (type != ENTITY_DIED)
                return;
        }
        this.playerId = readVarInt(dataInputStream);
        this.killerId = dataInputStream.readInt();
        if (ChatBot.getConfig().getProtocolVersion() >= 755 || type == ENTITY_DIED)//1.17 or the player actually died
            this.message = readString(dataInputStream);
        super.createPacket();
    }

    @Override
    public void apply() {
        ((ClientBoundPlayClientBoundPacketHandler)clientBoundPacketHandler).handlePlayerDeadPacket(this);
    }

    public int getType() {
        return type;
    }

    public int getPlayerId() {
        return playerId;
    }

    public int getKillerId() {
        return killerId;
    }

    public String getMessage() {
        return message;
    }
}
