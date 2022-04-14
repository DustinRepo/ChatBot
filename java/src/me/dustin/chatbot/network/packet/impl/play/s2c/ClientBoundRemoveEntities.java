package me.dustin.chatbot.network.packet.impl.play.s2c;

import me.dustin.chatbot.network.packet.Packet;
import me.dustin.chatbot.network.packet.ProtocolHandler;
import me.dustin.chatbot.network.packet.handler.ClientBoundPacketHandler;
import me.dustin.chatbot.network.packet.handler.PlayClientBoundPacketHandler;
import me.dustin.chatbot.network.packet.pipeline.PacketByteBuf;

public class ClientBoundRemoveEntities extends Packet.ClientBoundPacket {
    private final int[] entityIds;
    public ClientBoundRemoveEntities(PacketByteBuf packetByteBuf) {
        super(packetByteBuf);
        int c = ProtocolHandler.getCurrent().getProtocolVer() <= ProtocolHandler.getVersionFromName("1.7.10").getProtocolVer() ? packetByteBuf.readByte() : packetByteBuf.readVarInt();
        this.entityIds = new int[c];
        for (int i = 0; i < c; i++) {
            this.entityIds[i] = ProtocolHandler.getCurrent().getProtocolVer() <= ProtocolHandler.getVersionFromName("1.7.10").getProtocolVer() ?  packetByteBuf.readInt() : packetByteBuf.readVarInt();
        }
    }

    @Override
    public void apply(ClientBoundPacketHandler clientBoundPacketHandler) {
        ((PlayClientBoundPacketHandler)clientBoundPacketHandler).handleRemoveEntitiesPacket(this);
    }

    public int[] getEntityIds() {
        return entityIds;
    }
}
