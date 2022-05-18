package me.dustin.chatbot.network.packet.impl.play.s2c;

import me.dustin.chatbot.network.packet.Packet;
import me.dustin.chatbot.network.packet.handler.ClientBoundPacketHandler;
import me.dustin.chatbot.network.packet.handler.PlayClientBoundPacketHandler;
import me.dustin.chatbot.network.packet.pipeline.PacketByteBuf;

public class ClientBoundSetPreviewEnabledPacket extends Packet.ClientBoundPacket {
    private final boolean enabled;
    public ClientBoundSetPreviewEnabledPacket(PacketByteBuf packetByteBuf) {
        super(packetByteBuf);
        this.enabled = packetByteBuf.readBoolean();
    }

    @Override
    public void handlePacket(ClientBoundPacketHandler clientBoundPacketHandler) {
        ((PlayClientBoundPacketHandler)clientBoundPacketHandler).handleSetPreviewEnabledPacket(this);
    }

    public boolean isEnabled() {
        return enabled;
    }
}
