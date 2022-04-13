package me.dustin.chatbot.network.packet.s2c.play;

import me.dustin.chatbot.network.packet.Packet;
import me.dustin.chatbot.network.packet.ProtocolHandler;
import me.dustin.chatbot.network.packet.handler.ClientBoundPacketHandler;
import me.dustin.chatbot.network.packet.handler.PlayClientBoundPacketHandler;
import me.dustin.chatbot.network.packet.pipeline.PacketByteBuf;

import java.io.IOException;

public class ClientBoundCustomDataPacket extends Packet.ClientBoundPacket {
    private String channel;
    private PacketByteBuf data;
    public ClientBoundCustomDataPacket(ClientBoundPacketHandler clientBoundPacketHandler) {
        super(clientBoundPacketHandler);
    }

    @Override
    public void createPacket(PacketByteBuf packetByteBuf) throws IOException {
        this.channel = packetByteBuf.readString();
        int dataSize = ProtocolHandler.getCurrent().getProtocolVer() <= ProtocolHandler.getVersionFromName("1.7.10").getProtocolVer() ? packetByteBuf.readShort() : packetByteBuf.readableBytes();
        if (dataSize < 0 || dataSize > 0x100000) {
            throw new IllegalArgumentException("CustomDataPacket too big!");
        }
        this.data = new PacketByteBuf(packetByteBuf.readBytes(dataSize));
        super.createPacket(packetByteBuf);
    }

    @Override
    public void apply() {
        ((PlayClientBoundPacketHandler)clientBoundPacketHandler).handleCustomDataPacket(this);
    }

    public String getChannel() {
        return channel;
    }

    public PacketByteBuf getData() {
        return data;
    }
}
