package me.dustin.chatbot.network.packet.s2c.play;

import me.dustin.chatbot.network.ClientBoundPacketHandler;
import me.dustin.chatbot.network.packet.Packet;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

public class ClientBoundPlayerDeadPacket extends Packet.ClientBoundPacket {
    private int playerId;
    private int killerId;
    private String message;
    public ClientBoundPlayerDeadPacket(ClientBoundPacketHandler clientBoundPacketHandler) {
        super(0x35, clientBoundPacketHandler);
    }

    @Override
    public void createPacket(ByteArrayInputStream byteArrayInputStream) throws IOException {
        DataInputStream dataInputStream = new DataInputStream(byteArrayInputStream);
        this.playerId = readVarInt(dataInputStream);
        this.killerId = dataInputStream.readInt();
        this.message = readString(dataInputStream);
        super.createPacket();
    }

    @Override
    public void apply() {
        clientBoundPacketHandler.handlePlayerDeadPacket(this);
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
