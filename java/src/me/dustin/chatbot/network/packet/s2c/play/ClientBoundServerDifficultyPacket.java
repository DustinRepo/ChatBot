package me.dustin.chatbot.network.packet.s2c.play;

import me.dustin.chatbot.network.packet.Packet;
import me.dustin.chatbot.network.packet.ProtocolHandler;
import me.dustin.chatbot.network.packet.handler.ClientBoundPacketHandler;
import me.dustin.chatbot.network.packet.handler.PlayClientBoundPacketHandler;
import me.dustin.chatbot.network.packet.pipeline.PacketByteBuf;
import me.dustin.chatbot.network.world.World;

import java.io.IOException;

public class ClientBoundServerDifficultyPacket extends Packet.ClientBoundPacket {
    private World.Difficulty difficulty;
    private boolean isLocked = false;
    public ClientBoundServerDifficultyPacket(ClientBoundPacketHandler clientBoundPacketHandler) {
        super(clientBoundPacketHandler);
    }

    @Override
    public void createPacket(PacketByteBuf packetByteBuf) throws IOException {
        this.difficulty = World.Difficulty.values()[packetByteBuf.readByte()];
        if (ProtocolHandler.getCurrent().getProtocolVer() >= ProtocolHandler.getVersionFromName("1.14.2").getProtocolVer())
            this.isLocked = packetByteBuf.readBoolean();
        super.createPacket(packetByteBuf);
    }

    @Override
    public void apply() {
        ((PlayClientBoundPacketHandler)clientBoundPacketHandler).handleServerDifficultyPacket(this);
    }

    public World.Difficulty getDifficulty() {
        return difficulty;
    }
}
