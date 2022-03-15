package me.dustin.chatbot.network.packet.s2c.login;

import me.dustin.chatbot.network.packet.Packet;
import me.dustin.chatbot.network.packet.handler.ClientBoundLoginClientBoundPacketHandler;
import me.dustin.chatbot.network.packet.handler.ClientBoundPacketHandler;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

public class ClientBoundPluginRequestPacket extends Packet.ClientBoundPacket {

    private int messageId;
    private String identifier;

    public ClientBoundPluginRequestPacket(ClientBoundPacketHandler clientBoundPacketHandler) {
        super(clientBoundPacketHandler);
    }

    @Override
    public void createPacket(DataInputStream dataInputStream) throws IOException {
        this.messageId = readVarInt(dataInputStream);
        this.identifier = readString(dataInputStream);
    }

    @Override
    public void apply() {
        ((ClientBoundLoginClientBoundPacketHandler)clientBoundPacketHandler).handlePluginRequestPacket(this);
    }

    public int getMessageId() {
        return messageId;
    }

    public String getIdentifier() {
        return identifier;
    }
}
