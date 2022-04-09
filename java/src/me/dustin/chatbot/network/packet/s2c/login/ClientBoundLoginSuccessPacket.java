package me.dustin.chatbot.network.packet.s2c.login;

import me.dustin.chatbot.ChatBot;
import me.dustin.chatbot.network.Protocols;
import me.dustin.chatbot.network.packet.pipeline.PacketByteBuf;
import me.dustin.chatbot.network.packet.Packet;
import me.dustin.chatbot.network.packet.handler.ClientBoundLoginClientBoundPacketHandler;
import me.dustin.chatbot.network.packet.handler.ClientBoundPacketHandler;

import java.io.IOException;
import java.util.UUID;

public class ClientBoundLoginSuccessPacket extends Packet.ClientBoundPacket {
    private UUID uuid;
    private String username;

    public ClientBoundLoginSuccessPacket(ClientBoundPacketHandler clientBoundPacketHandler) {
        super(0x02, clientBoundPacketHandler);
    }

    @Override
    public void createPacket(PacketByteBuf packetByteBuf) throws IOException {
        if (ChatBot.getConfig().getProtocolVersion() <= Protocols.V1_15_2.getProtocolVer()) {//1.15.2 and below
            String s = packetByteBuf.readString();
            String s1 = packetByteBuf.readString();
            this.uuid = s.length() > 0 ? UUID.fromString(s) : null;
            this.username = s1;
            return;
        }

        this.uuid = packetByteBuf.readUuid();
        this.username = packetByteBuf.readString();
    }

    @Override
    public void apply() {
        ((ClientBoundLoginClientBoundPacketHandler)clientBoundPacketHandler).handleLoginSuccess(this);
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getUsername() {
        return username;
    }
}
