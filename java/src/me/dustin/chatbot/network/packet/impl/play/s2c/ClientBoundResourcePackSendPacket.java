package me.dustin.chatbot.network.packet.impl.play.s2c;

import me.dustin.chatbot.network.ProtocolHandler;
import me.dustin.chatbot.network.packet.pipeline.PacketByteBuf;
import me.dustin.chatbot.network.packet.Packet;
import me.dustin.chatbot.network.packet.handler.ClientBoundPacketHandler;
import me.dustin.chatbot.network.packet.handler.PlayClientBoundPacketHandler;

public class ClientBoundResourcePackSendPacket extends Packet.ClientBoundPacket {

    private final String url;
    private final String hash;
    private final boolean forced;
    private final boolean hasPrompt;
    private final String prompt;

    public ClientBoundResourcePackSendPacket(PacketByteBuf packetByteBuf) {
        super(packetByteBuf);
        this.url = packetByteBuf.readString();
        this.hash = packetByteBuf.readString();
        if (ProtocolHandler.getCurrent().getProtocolVer() > ProtocolHandler.getVersionFromName("1.12.2").getProtocolVer()) {
            this.forced = packetByteBuf.readBoolean();
            this.hasPrompt = packetByteBuf.readBoolean();
            if (hasPrompt)
                this.prompt = packetByteBuf.readString();
            else
                this.prompt = "";
        } else {
            this.forced = true;
            this.hasPrompt = false;
            this.prompt = "";
        }
    }

    @Override
    public void handlePacket(ClientBoundPacketHandler clientBoundPacketHandler) {
        ((PlayClientBoundPacketHandler)clientBoundPacketHandler).handleResourcePackPacket(this);
    }

    public String getUrl() {
        return url;
    }

    public String getHash() {
        return hash;
    }

    public boolean isForced() {
        return forced;
    }

    public boolean isHasPrompt() {
        return hasPrompt;
    }

    public String getPrompt() {
        return prompt;
    }
}
