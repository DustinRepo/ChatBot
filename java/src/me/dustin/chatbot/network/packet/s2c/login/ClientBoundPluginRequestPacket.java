package me.dustin.chatbot.network.packet.s2c.login;

import me.dustin.chatbot.network.packet.pipeline.PacketByteBuf;
import me.dustin.chatbot.network.packet.Packet;
import me.dustin.chatbot.network.packet.handler.LoginClientBoundPacketHandler;
import me.dustin.chatbot.network.packet.handler.ClientBoundPacketHandler;

import java.io.IOException;

public class ClientBoundPluginRequestPacket extends Packet.ClientBoundPacket {

    private int messageId;
    private String identifier;

    public ClientBoundPluginRequestPacket(ClientBoundPacketHandler clientBoundPacketHandler) {
        super(clientBoundPacketHandler);
    }

    public void createPacket(PacketByteBuf packetByteBuf) throws IOException {
        this.messageId = packetByteBuf.readVarInt();
        this.identifier = packetByteBuf.readString();
    }

    @Override
    public void apply() {
        ((LoginClientBoundPacketHandler)clientBoundPacketHandler).handlePluginRequestPacket(this);
    }

    public int getMessageId() {
        return messageId;
    }

    public String getIdentifier() {
        return identifier;
    }
}
