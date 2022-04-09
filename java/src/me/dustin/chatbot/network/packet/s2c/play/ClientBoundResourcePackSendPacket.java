package me.dustin.chatbot.network.packet.s2c.play;

import me.dustin.chatbot.ChatBot;
import me.dustin.chatbot.network.packet.PacketIDs;
import me.dustin.chatbot.network.packet.pipeline.PacketByteBuf;
import me.dustin.chatbot.network.Protocols;
import me.dustin.chatbot.network.packet.Packet;
import me.dustin.chatbot.network.packet.handler.ClientBoundPacketHandler;
import me.dustin.chatbot.network.packet.handler.ClientBoundPlayClientBoundPacketHandler;

import java.io.IOException;

public class ClientBoundResourcePackSendPacket extends Packet.ClientBoundPacket {

    private String url;
    private String hash;
    private boolean forced = true;//assume forced just incase
    private boolean hasPrompt;
    private String prompt;

    public ClientBoundResourcePackSendPacket(ClientBoundPacketHandler clientBoundPacketHandler) {
        super(PacketIDs.ClientBound.RESOURCE_PACK_SEND.getPacketId(), clientBoundPacketHandler);
    }

    @Override
    public void createPacket(PacketByteBuf packetByteBuf) throws IOException {
        this.url = packetByteBuf.readString();
        this.hash = packetByteBuf.readString();
        if (ChatBot.getConfig().getProtocolVersion() > Protocols.V1_12_2.getProtocolVer()) {
            this.forced = packetByteBuf.readBoolean();
            this.hasPrompt = packetByteBuf.readBoolean();
            if (hasPrompt) {
                this.prompt = packetByteBuf.readString();
            }
        }
    }

    @Override
    public void apply() {
        ((ClientBoundPlayClientBoundPacketHandler)clientBoundPacketHandler).handleResourcePackPacket(this);
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
