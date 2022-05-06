package me.dustin.chatbot.network.packet.impl.play.c2s;

import me.dustin.chatbot.network.ProtocolHandler;
import me.dustin.chatbot.network.key.SaltAndSig;
import me.dustin.chatbot.network.packet.Packet;
import me.dustin.chatbot.network.packet.pipeline.PacketByteBuf;

import java.time.Instant;

public class ServerBoundCommandPacket extends Packet {
    private final String command;
    private final Instant timestamp;
    private final SaltAndSig.SaltAndSigs saltAndSigs;
    public ServerBoundCommandPacket(String command, Instant timestamp, SaltAndSig.SaltAndSigs saltAndSigs) {
        super(ProtocolHandler.getCurrent().getPacketId(ProtocolHandler.NetworkSide.SERVERBOUND, "command_message"));
        this.command = command;
        this.timestamp = timestamp;
        this.saltAndSigs = saltAndSigs;
    }

    @Override
    public void createPacket(PacketByteBuf packetByteBuf) {
        packetByteBuf.writeString(command);
        packetByteBuf.writeLong(timestamp.getEpochSecond());
        saltAndSigs.write(packetByteBuf);
    }
}
