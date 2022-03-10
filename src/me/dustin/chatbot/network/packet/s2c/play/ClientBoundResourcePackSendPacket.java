package me.dustin.chatbot.network.packet.s2c.play;

import me.dustin.chatbot.network.packet.Packet;
import me.dustin.chatbot.network.packet.handler.ClientBoundPacketHandler;
import me.dustin.chatbot.network.packet.handler.ClientBoundPlayClientBoundPacketHandler;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

public class ClientBoundResourcePackSendPacket extends Packet.ClientBoundPacket {

    private String url;
    private String hash;
    private boolean forced;
    private boolean hasPrompt;
    private String prompt;

    public ClientBoundResourcePackSendPacket(ClientBoundPacketHandler clientBoundPacketHandler) {
        super(clientBoundPacketHandler);
    }

    @Override
    public void createPacket(ByteArrayInputStream byteArrayInputStream) throws IOException {
        DataInputStream dataInputStream = new DataInputStream(byteArrayInputStream);
        url = readString(dataInputStream);
        hash = readString(dataInputStream);
        forced = dataInputStream.readBoolean();
        hasPrompt = dataInputStream.readBoolean();
        if (hasPrompt) {
            prompt = readString(dataInputStream);
        }
        super.createPacket(byteArrayInputStream);
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
