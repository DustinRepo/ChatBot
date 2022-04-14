package me.dustin.chatbot.network.packet.s2c.login;

import me.dustin.chatbot.network.packet.ProtocolHandler;
import me.dustin.chatbot.network.packet.pipeline.PacketByteBuf;
import me.dustin.chatbot.network.packet.Packet;
import me.dustin.chatbot.network.packet.handler.LoginClientBoundPacketHandler;
import me.dustin.chatbot.network.packet.handler.ClientBoundPacketHandler;

import java.util.UUID;

public class ClientBoundLoginSuccessPacket extends Packet.ClientBoundPacket {
    private final UUID uuid;
    private final String username;

    public ClientBoundLoginSuccessPacket(PacketByteBuf packetByteBuf) {
        super(packetByteBuf);
        if (ProtocolHandler.getCurrent().getProtocolVer() <= ProtocolHandler.getVersionFromName("1.15.1").getProtocolVer()) {
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
    public void apply(ClientBoundPacketHandler clientBoundPacketHandler) {
        ((LoginClientBoundPacketHandler)clientBoundPacketHandler).handleLoginSuccess(this);
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getUsername() {
        return username;
    }
}
