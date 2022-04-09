package me.dustin.chatbot.network.packet.s2c.play;

import me.dustin.chatbot.ChatBot;
import me.dustin.chatbot.network.Protocols;
import me.dustin.chatbot.network.packet.PacketIDs;
import me.dustin.chatbot.network.packet.pipeline.PacketByteBuf;
import me.dustin.chatbot.network.packet.Packet;
import me.dustin.chatbot.network.packet.handler.ClientBoundPlayClientBoundPacketHandler;
import me.dustin.chatbot.network.packet.handler.ClientBoundPacketHandler;

import java.io.IOException;

public class ClientBoundKeepAlivePacket extends Packet.ClientBoundPacket {

    private long id;
    public ClientBoundKeepAlivePacket(ClientBoundPacketHandler clientBoundPacketHandler) {
        super(PacketIDs.ClientBound.KEEP_ALIVE.getPacketId(), clientBoundPacketHandler);
    }

    @Override
    public void createPacket(PacketByteBuf packetByteBuf) throws IOException {
        if (ChatBot.getConfig().getProtocolVersion() <= Protocols.V1_7_10.getProtocolVer())
            this.id = packetByteBuf.readInt();
        else if (ChatBot.getConfig().getProtocolVersion() <= Protocols.V1_12_1.getProtocolVer())//in 1.12.1 and below keepalive id is an int, not a long
            this.id = packetByteBuf.readVarInt();
        else
            this.id = packetByteBuf.readLong();
    }

    public long getId() {
        return id;
    }

    @Override
    public void apply() {
        ((ClientBoundPlayClientBoundPacketHandler)clientBoundPacketHandler).handleKeepAlivePacket(this);
    }
}
