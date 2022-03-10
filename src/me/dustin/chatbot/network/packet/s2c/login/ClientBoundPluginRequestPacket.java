package me.dustin.chatbot.network.packet.s2c.login;

import me.dustin.chatbot.helper.GeneralHelper;
import me.dustin.chatbot.network.ClientBoundPacketHandler;
import me.dustin.chatbot.network.packet.Packet;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

public class ClientBoundPluginRequestPacket extends Packet.ClientBoundPacket {

    private int messageId;
    private String identifier;

    public ClientBoundPluginRequestPacket(ClientBoundPacketHandler clientBoundPacketHandler) {
        super(0x04, clientBoundPacketHandler);
    }

    @Override
    public void createPacket(ByteArrayInputStream byteArrayInputStream) throws IOException {
        DataInputStream dataInputStream = new DataInputStream(byteArrayInputStream);
        messageId = readVarInt(dataInputStream);
        identifier = readString(dataInputStream);
        super.createPacket(byteArrayInputStream);
    }

    @Override
    public void apply() {
        clientBoundPacketHandler.handlePluginRequestPacket(this);
    }

    public int getMessageId() {
        return messageId;
    }

    public String getIdentifier() {
        return identifier;
    }
}
