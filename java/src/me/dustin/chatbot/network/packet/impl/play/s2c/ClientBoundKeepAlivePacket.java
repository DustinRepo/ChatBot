package me.dustin.chatbot.network.packet.impl.play.s2c;

import me.dustin.chatbot.network.packet.ProtocolHandler;
import me.dustin.chatbot.network.packet.pipeline.PacketByteBuf;
import me.dustin.chatbot.network.packet.Packet;
import me.dustin.chatbot.network.packet.handler.PlayClientBoundPacketHandler;
import me.dustin.chatbot.network.packet.handler.ClientBoundPacketHandler;

public class ClientBoundKeepAlivePacket extends Packet.ClientBoundPacket {

    private final long id;

    public ClientBoundKeepAlivePacket(PacketByteBuf packetByteBuf) {
        super(packetByteBuf);
        if (ProtocolHandler.getCurrent().getProtocolVer() <= ProtocolHandler.getVersionFromName("1.7.10").getProtocolVer())
            this.id = packetByteBuf.readInt();
        else if (ProtocolHandler.getCurrent().getProtocolVer() <= ProtocolHandler.getVersionFromName("1.12.1").getProtocolVer())//in 1.12.1 and below keepalive id is an int, not a long
            this.id = packetByteBuf.readVarInt();
        else
            this.id = packetByteBuf.readLong();
    }

    public long getId() {
        return id;
    }

    @Override
    public void apply(ClientBoundPacketHandler clientBoundPacketHandler) {
        ((PlayClientBoundPacketHandler)clientBoundPacketHandler).handleKeepAlivePacket(this);
    }
}
