package me.dustin.chatbot.network.packet.impl.play.s2c;

import me.dustin.chatbot.chat.ChatMessage;
import me.dustin.chatbot.network.packet.Packet;
import me.dustin.chatbot.network.packet.handler.ClientBoundPacketHandler;
import me.dustin.chatbot.network.packet.handler.PlayClientBoundPacketHandler;
import me.dustin.chatbot.network.packet.pipeline.PacketByteBuf;

public class ClientBoundServerDataPacket extends Packet.ClientBoundPacket {
    private final ChatMessage motd;
    private final String encodedIcon;
    private final boolean hasChatPreview;
    public ClientBoundServerDataPacket(PacketByteBuf packetByteBuf) {
        super(packetByteBuf);
        if (packetByteBuf.readBoolean())
            motd = ChatMessage.of(packetByteBuf.readString());
        else
            motd = null;
        if (packetByteBuf.readBoolean())
            encodedIcon = packetByteBuf.readString();
        else
            encodedIcon = null;
        hasChatPreview = packetByteBuf.readBoolean();
    }

    @Override
    public void handlePacket(ClientBoundPacketHandler clientBoundPacketHandler) {
        ((PlayClientBoundPacketHandler)clientBoundPacketHandler).handleServerDataPacket(this);
    }

    public ChatMessage getMotd() {
        return motd;
    }

    public String getEncodedIcon() {
        return encodedIcon;
    }

    public boolean isHasChatPreview() {
        return hasChatPreview;
    }
}
