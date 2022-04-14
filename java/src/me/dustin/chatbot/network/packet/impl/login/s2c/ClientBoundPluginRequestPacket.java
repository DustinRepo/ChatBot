package me.dustin.chatbot.network.packet.impl.login.s2c;

import me.dustin.chatbot.network.packet.pipeline.PacketByteBuf;
import me.dustin.chatbot.network.packet.Packet;
import me.dustin.chatbot.network.packet.handler.LoginClientBoundPacketHandler;
import me.dustin.chatbot.network.packet.handler.ClientBoundPacketHandler;

public class ClientBoundPluginRequestPacket extends Packet.ClientBoundPacket {

    private final int messageId;
    private final String identifier;

    public ClientBoundPluginRequestPacket(PacketByteBuf packetByteBuf) {
        super(packetByteBuf);
        this.messageId = packetByteBuf.readVarInt();
        this.identifier = packetByteBuf.readString();
    }

    @Override
    public void apply(ClientBoundPacketHandler clientBoundPacketHandler) {
        ((LoginClientBoundPacketHandler)clientBoundPacketHandler).handlePluginRequestPacket(this);
    }

    public int getMessageId() {
        return messageId;
    }

    public String getIdentifier() {
        return identifier;
    }
}
