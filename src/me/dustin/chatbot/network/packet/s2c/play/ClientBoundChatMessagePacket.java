package me.dustin.chatbot.network.packet.s2c.play;

import me.dustin.chatbot.network.ClientBoundPacketHandler;
import me.dustin.chatbot.network.packet.Packet;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.UUID;

public class ClientBoundChatMessagePacket extends Packet.ClientBoundPacket {
    public static int MESSAGE_TYPE_CHAT = 0, MESSAGE_TYPE_SYSTEM = 1, MESSAGE_TYPE_GAME_INFO = 2;
    private String message;
    private byte type;
    private UUID sender;

    public ClientBoundChatMessagePacket(ClientBoundPacketHandler clientBoundPacketHandler) {
        super(0x0F, clientBoundPacketHandler);
    }

    @Override
    public void createPacket(ByteArrayInputStream byteArrayInputStream) throws IOException {
        DataInputStream dataInputStream = new DataInputStream(byteArrayInputStream);
        message = readString(dataInputStream);
        type = dataInputStream.readByte();
        sender = readUUID(dataInputStream);
    }

    @Override
    public void apply() {
        clientBoundPacketHandler.handleChatMessage(this);
    }

    public String getMessage() {
        return message;
    }

    public byte getType() {
        return type;
    }

    public UUID getSender() {
        return sender;
    }
}
