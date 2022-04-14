package me.dustin.chatbot.network.packet.impl.play.s2c;

import me.dustin.chatbot.network.packet.Packet;
import me.dustin.chatbot.network.packet.ProtocolHandler;
import me.dustin.chatbot.network.packet.handler.ClientBoundPacketHandler;
import me.dustin.chatbot.network.packet.handler.PlayClientBoundPacketHandler;
import me.dustin.chatbot.network.packet.pipeline.PacketByteBuf;
import me.dustin.chatbot.world.World;

public class ClientBoundServerDifficultyPacket extends Packet.ClientBoundPacket {
    private final World.Difficulty difficulty;
    private final boolean isLocked;

    public ClientBoundServerDifficultyPacket(PacketByteBuf packetByteBuf) {
        super(packetByteBuf);
        this.difficulty = World.Difficulty.values()[packetByteBuf.readByte()];
        if (ProtocolHandler.getCurrent().getProtocolVer() >= ProtocolHandler.getVersionFromName("1.14.2").getProtocolVer())
            this.isLocked = packetByteBuf.readBoolean();
        else
            this.isLocked = false;
    }

    @Override
    public void apply(ClientBoundPacketHandler clientBoundPacketHandler) {
        ((PlayClientBoundPacketHandler)clientBoundPacketHandler).handleServerDifficultyPacket(this);
    }

    public World.Difficulty getDifficulty() {
        return difficulty;
    }

    public boolean isLocked() {
        return isLocked;
    }
}
