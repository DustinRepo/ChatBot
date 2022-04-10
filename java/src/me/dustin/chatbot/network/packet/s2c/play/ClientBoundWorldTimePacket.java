package me.dustin.chatbot.network.packet.s2c.play;

import me.dustin.chatbot.network.packet.PacketIDs;
import me.dustin.chatbot.network.packet.pipeline.PacketByteBuf;
import me.dustin.chatbot.network.packet.Packet;
import me.dustin.chatbot.network.packet.handler.PlayClientBoundPacketHandler;
import me.dustin.chatbot.network.packet.handler.ClientBoundPacketHandler;

import java.io.IOException;

public class ClientBoundWorldTimePacket extends Packet.ClientBoundPacket {
    private long worldAge;
    private long timeOfDay;
    public ClientBoundWorldTimePacket(ClientBoundPacketHandler clientBoundPacketHandler) {
        super(PacketIDs.ClientBound.WORLD_TIME.getPacketId(), clientBoundPacketHandler);
    }

    @Override
    public void createPacket(PacketByteBuf packetByteBuf) throws IOException {
        this.worldAge = packetByteBuf.readLong();
        this.timeOfDay = packetByteBuf.readLong();
    }

    @Override
    public void apply() {
        ((PlayClientBoundPacketHandler)clientBoundPacketHandler).handleWorldTimePacket(this);
    }

    public long getWorldAge() {
        return worldAge;
    }

    public long getTimeOfDay() {
        return timeOfDay;
    }
}
