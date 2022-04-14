package me.dustin.chatbot.network.packet.impl.play.s2c;

import me.dustin.chatbot.network.packet.Packet;
import me.dustin.chatbot.network.packet.ProtocolHandler;
import me.dustin.chatbot.network.packet.handler.ClientBoundPacketHandler;
import me.dustin.chatbot.network.packet.handler.PlayClientBoundPacketHandler;
import me.dustin.chatbot.network.packet.pipeline.PacketByteBuf;

public class ClientBoundCustomDataPacket extends Packet.ClientBoundPacket {
    private final String channel;
    private final PacketByteBuf data;
    public ClientBoundCustomDataPacket(PacketByteBuf packetByteBuf) {
        super(packetByteBuf);
        this.channel = packetByteBuf.readString();
        int dataSize = ProtocolHandler.getCurrent().getProtocolVer() <= ProtocolHandler.getVersionFromName("1.7.10").getProtocolVer() ? packetByteBuf.readShort() : packetByteBuf.readableBytes();
        if (dataSize < 0 || dataSize > 0x100000) {
            throw new IllegalArgumentException("CustomDataPacket too big!");
        }
        this.data = new PacketByteBuf(packetByteBuf.readBytes(dataSize));
    }

    @Override
    public void apply(ClientBoundPacketHandler clientBoundPacketHandler) {
        ((PlayClientBoundPacketHandler)clientBoundPacketHandler).handleCustomDataPacket(this);
    }

    public String getChannel() {
        return channel;
    }

    public PacketByteBuf getData() {
        return data;
    }
}
