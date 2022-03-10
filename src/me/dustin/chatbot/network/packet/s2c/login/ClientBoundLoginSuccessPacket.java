package me.dustin.chatbot.network.packet.s2c.login;

import me.dustin.chatbot.network.ClientBoundPacketHandler;
import me.dustin.chatbot.network.packet.Packet;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.UUID;

public class ClientBoundLoginSuccessPacket extends Packet.ClientBoundPacket {
    public UUID uuid;
    public String username;

    public ClientBoundLoginSuccessPacket(ClientBoundPacketHandler clientBoundPacketHandler) {
        super(0x02, clientBoundPacketHandler);
    }

    @Override
    public void createPacket(ByteArrayInputStream byteArrayInputStream) throws IOException {
        DataInputStream dataInputStream = new DataInputStream(byteArrayInputStream);

        this.uuid = readUUID(dataInputStream);
        this.username = readString(dataInputStream);

        super.createPacket(byteArrayInputStream);
    }

    @Override
    public void apply() {
        clientBoundPacketHandler.handleLoginSuccess(this);
    }
}
