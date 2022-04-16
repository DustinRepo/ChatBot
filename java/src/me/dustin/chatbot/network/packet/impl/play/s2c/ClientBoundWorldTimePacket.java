package me.dustin.chatbot.network.packet.impl.play.s2c;

import me.dustin.chatbot.network.packet.pipeline.PacketByteBuf;
import me.dustin.chatbot.network.packet.Packet;
import me.dustin.chatbot.network.packet.handler.PlayClientBoundPacketHandler;
import me.dustin.chatbot.network.packet.handler.ClientBoundPacketHandler;

public class ClientBoundWorldTimePacket extends Packet.ClientBoundPacket {
    private long worldAge;
    private long timeOfDay;

    public ClientBoundWorldTimePacket(PacketByteBuf packetByteBuf) {
        super(packetByteBuf);
        this.worldAge = packetByteBuf.readLong();
        this.timeOfDay = packetByteBuf.readLong();
    }

    @Override
    public void handlePacket(ClientBoundPacketHandler clientBoundPacketHandler) {
        ((PlayClientBoundPacketHandler)clientBoundPacketHandler).handleWorldTimePacket(this);
    }

    public long getWorldAge() {
        return worldAge;
    }

    public long getTimeOfDay() {
        return timeOfDay;
    }
}
