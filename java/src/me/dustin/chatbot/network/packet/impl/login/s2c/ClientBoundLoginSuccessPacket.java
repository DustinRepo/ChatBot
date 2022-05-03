package me.dustin.chatbot.network.packet.impl.login.s2c;

import me.dustin.chatbot.entity.player.PlayerInfo;
import me.dustin.chatbot.network.ProtocolHandler;
import me.dustin.chatbot.network.packet.pipeline.PacketByteBuf;
import me.dustin.chatbot.network.packet.Packet;
import me.dustin.chatbot.network.packet.handler.LoginClientBoundPacketHandler;
import me.dustin.chatbot.network.packet.handler.ClientBoundPacketHandler;

import java.util.ArrayList;
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
        if (ProtocolHandler.getCurrent().getProtocolVer() > ProtocolHandler.getVersionFromName("1.18.2").getProtocolVer()) {
            int propertyListSize = packetByteBuf.readVarInt();
            ArrayList<PlayerInfo.PlayerProperty> properties = new ArrayList<>();
            for (int ii = 0; ii < propertyListSize; ii++) {
                String pName = packetByteBuf.readString();
                String pValue = packetByteBuf.readString();
                boolean isSigned = packetByteBuf.readBoolean();
                String signature = "";
                if (isSigned) {
                    signature = packetByteBuf.readString();
                }
                properties.add(new PlayerInfo.PlayerProperty(pName, pValue, isSigned, signature));
            }
            getClientConnection().getClientPlayer().getProperties().addAll(properties);
        }
    }

    @Override
    public void handlePacket(ClientBoundPacketHandler clientBoundPacketHandler) {
        ((LoginClientBoundPacketHandler)clientBoundPacketHandler).handleLoginSuccess(this);
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getUsername() {
        return username;
    }
}
