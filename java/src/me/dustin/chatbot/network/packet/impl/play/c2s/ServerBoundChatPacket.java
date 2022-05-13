package me.dustin.chatbot.network.packet.impl.play.c2s;

import me.dustin.chatbot.network.key.SaltAndSig;
import me.dustin.chatbot.network.packet.Packet;
import me.dustin.chatbot.network.ProtocolHandler;
import me.dustin.chatbot.network.packet.pipeline.PacketByteBuf;

import java.io.IOException;
import java.time.Instant;

public class ServerBoundChatPacket extends Packet {
    private final String message;
    private final Instant instant;
    private final SaltAndSig saltAndSig;
    private final boolean preview;
    public ServerBoundChatPacket(String message, Instant instant, SaltAndSig saltAndSig, boolean preview) {
        super(ProtocolHandler.getCurrent().getPacketId(ProtocolHandler.NetworkSide.SERVERBOUND, "chat_message"));
        if (ProtocolHandler.getCurrent().getProtocolVer() >= ProtocolHandler.getVersionFromName("1.11").getProtocolVer()) {
            if (message.length() > 256) {
                message = message.substring(0, 256);
            }
        } else {
            if (message.length() > 100) {
                message = message.substring(0, 100);
            }
        }
        this.message = message;
        this.instant = instant;
        this.saltAndSig = saltAndSig;
        this.preview = preview;
    }

    @Override
    public void createPacket(PacketByteBuf packetByteBuf) throws IOException {
        packetByteBuf.writeString(this.message);
        if (ProtocolHandler.getCurrent().getProtocolVer() > ProtocolHandler.getVersionFromName("1.18.2").getProtocolVer()) {
            packetByteBuf.writeLong(this.instant.toEpochMilli());
            this.saltAndSig.write(packetByteBuf);
            packetByteBuf.writeBoolean(this.preview);
        }
    }
}
