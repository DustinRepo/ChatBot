package me.dustin.chatbot.network.packet.s2c.play;

import me.dustin.chatbot.nbt.NbtCompound;
import me.dustin.chatbot.network.packet.ProtocolHandler;
import me.dustin.chatbot.network.packet.pipeline.PacketByteBuf;
import me.dustin.chatbot.network.packet.Packet;
import me.dustin.chatbot.network.packet.handler.ClientBoundPacketHandler;
import me.dustin.chatbot.network.packet.handler.PlayClientBoundPacketHandler;
import me.dustin.chatbot.network.player.OtherPlayer;
import me.dustin.chatbot.network.world.World;

import java.io.IOException;

public class ClientBoundJoinGamePacket extends Packet.ClientBoundPacket {
    private int entityId;
    private boolean isHardcore;
    private OtherPlayer.GameMode gameMode;
    private OtherPlayer.GameMode previousGameMode;
    private String[] dimNames;
    private NbtCompound dimensionCodec;
    private NbtCompound dimensionNBT;
    private World.Dimension dimension;
    private long hashedSeed;
    private int maxPlayers;
    private int viewDistance;
    private int simulationDistance;
    private boolean reducedDebugInfo;
    private boolean enabledRespawnScreen = true;
    private boolean isDebug;
    private boolean isFlat;

    private World.Difficulty difficulty;

    public ClientBoundJoinGamePacket(ClientBoundPacketHandler clientBoundPacketHandler) {
        super(clientBoundPacketHandler);
    }

    @Override
    public void createPacket(PacketByteBuf packetByteBuf) throws IOException {
        this.entityId = packetByteBuf.readInt();

        if (ProtocolHandler.getCurrent().getProtocolVer() <= ProtocolHandler.getVersionFromName("1.15.2").getProtocolVer()) {
            this.gameMode = OtherPlayer.GameMode.get(packetByteBuf.readByte());
            this.dimension = World.Dimension.get(ProtocolHandler.getCurrent().getProtocolVer() < ProtocolHandler.getVersionFromName("1.10.2").getProtocolVer() ? packetByteBuf.readByte() : packetByteBuf.readInt());
            if (ProtocolHandler.getCurrent().getProtocolVer() >= ProtocolHandler.getVersionFromName("1.14").getProtocolVer())
                this.hashedSeed = packetByteBuf.readLong();
            if (ProtocolHandler.getCurrent().getProtocolVer() <= ProtocolHandler.getVersionFromName("1.13").getProtocolVer())
                this.difficulty = World.Difficulty.values()[packetByteBuf.readByte()];//difficulty on 1.13 or below
            this.maxPlayers = packetByteBuf.readByte();
            this.isFlat = packetByteBuf.readString().contains("flat");
            if (ProtocolHandler.getCurrent().getProtocolVer() > ProtocolHandler.getVersionFromName("1.13.2").getProtocolVer()) {
                this.viewDistance = packetByteBuf.readVarInt();
                this.simulationDistance = this.viewDistance - 1;
            }
            if (ProtocolHandler.getCurrent().getProtocolVer() > ProtocolHandler.getVersionFromName("1.8.9").getProtocolVer())
                this.reducedDebugInfo = packetByteBuf.readBoolean();
            if (ProtocolHandler.getCurrent().getProtocolVer() >= ProtocolHandler.getVersionFromName("1.14").getProtocolVer())
                this.enabledRespawnScreen = packetByteBuf.readBoolean();
            return;
        }

        this.isHardcore = packetByteBuf.readBoolean();
        this.gameMode = OtherPlayer.GameMode.get(packetByteBuf.readByte());
        this.previousGameMode = OtherPlayer.GameMode.get(packetByteBuf.readByte());
        int worldCount = packetByteBuf.readVarInt();
        this.dimNames = new String[worldCount];
        for (int i = 0; i < worldCount; i++) {
            this.dimNames[i] = packetByteBuf.readString();
        }
        this.dimensionCodec = (NbtCompound) packetByteBuf.readNbt();
        this.dimensionNBT = (NbtCompound) packetByteBuf.readNbt();
        this.dimension = World.Dimension.get(packetByteBuf.readString());
        this.hashedSeed = packetByteBuf.readLong();
        this.maxPlayers = packetByteBuf.readVarInt();
        this.viewDistance = packetByteBuf.readVarInt();
        if (ProtocolHandler.getCurrent().getProtocolVer() >= ProtocolHandler.getVersionFromName("1.18.1").getProtocolVer())
            this.simulationDistance = packetByteBuf.readVarInt();
        else
            this.simulationDistance = this.viewDistance - 1;
        this.reducedDebugInfo = packetByteBuf.readBoolean();
        this.enabledRespawnScreen = packetByteBuf.readBoolean();
        this.isDebug = packetByteBuf.readBoolean();
        this.isFlat = packetByteBuf.readBoolean();
    }

    @Override
    public void apply() {
        ((PlayClientBoundPacketHandler)clientBoundPacketHandler).handleJoinGamePacket(this);
    }

    public int getEntityId() {
        return entityId;
    }

    public boolean isHardcore() {
        return isHardcore;
    }

    public OtherPlayer.GameMode getGameMode() {
        return gameMode;
    }

    public OtherPlayer.GameMode getPreviousGameMode() {
        return previousGameMode;
    }

    public String[] getDimNames() {
        return dimNames;
    }

    public NbtCompound getDimensionCodec() {
        return dimensionCodec;
    }

    public NbtCompound getDimensionNbt() {
        return dimensionNBT;
    }

    public World.Dimension getDimension() {
        return dimension;
    }

    public World.Difficulty getDifficulty() {
        return difficulty;
    }

    public long getHashedSeed() {
        return hashedSeed;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public int getViewDistance() {
        return viewDistance;
    }

    public int getSimulationDistance() {
        return simulationDistance;
    }

    public boolean isReducedDebugInfo() {
        return reducedDebugInfo;
    }

    public boolean isEnabledRespawnScreen() {
        return enabledRespawnScreen;
    }

    public boolean isDebug() {
        return isDebug;
    }

    public boolean isFlat() {
        return isFlat;
    }
}
