package me.dustin.chatbot.network.packet.s2c.login;

import me.dustin.chatbot.ChatBot;
import me.dustin.chatbot.network.packet.Packet;
import me.dustin.chatbot.network.packet.handler.ClientBoundLoginClientBoundPacketHandler;
import me.dustin.chatbot.network.packet.handler.ClientBoundPacketHandler;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.UUID;

public class ClientBoundLoginSuccessPacket extends Packet.ClientBoundPacket {
    private UUID uuid;
    private String username;

    public ClientBoundLoginSuccessPacket(ClientBoundPacketHandler clientBoundPacketHandler) {
        super(clientBoundPacketHandler);
    }

    @Override
    public void createPacket(ByteArrayInputStream byteArrayInputStream) throws IOException {
        DataInputStream dataInputStream = new DataInputStream(byteArrayInputStream);

        if (ChatBot.getConfig().getProtocolVersion() == 340) {//1.12.2
            String s = readString(dataInputStream);
            String s1 = readString(dataInputStream);
            uuid = s.length() > 0 ? UUID.fromString(s) : null;
            this.username = s1;
            return;
        }

        this.uuid = readUUID(dataInputStream);
        this.username = readString(dataInputStream);

        super.createPacket(byteArrayInputStream);
    }

    @Override
    public void apply() {
        ((ClientBoundLoginClientBoundPacketHandler)clientBoundPacketHandler).handleLoginSuccess(this);
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getUsername() {
        return username;
    }
}
