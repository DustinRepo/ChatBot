package me.dustin.chatbot.network.packet.s2c.play;

import me.dustin.chatbot.ChatBot;
import me.dustin.chatbot.network.packet.Packet;
import me.dustin.chatbot.network.packet.handler.ClientBoundPacketHandler;
import me.dustin.chatbot.network.packet.handler.ClientBoundPlayClientBoundPacketHandler;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

public class ClientBoundResourcePackSendPacket extends Packet.ClientBoundPacket {

    private String url;
    private String hash;
    private boolean forced = true;//assume forced just incase
    private boolean hasPrompt;
    private String prompt;

    public ClientBoundResourcePackSendPacket(ClientBoundPacketHandler clientBoundPacketHandler) {
        super(clientBoundPacketHandler);
    }

    @Override
    public void createPacket(DataInputStream dataInputStream) throws IOException {
        this.url = readString(dataInputStream);
        this.hash = readString(dataInputStream);
        if (ChatBot.getConfig().getProtocolVersion() > 340) {
            this.forced = dataInputStream.readBoolean();
            this.hasPrompt = dataInputStream.readBoolean();
            if (hasPrompt) {
                this.prompt = readString(dataInputStream);
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
