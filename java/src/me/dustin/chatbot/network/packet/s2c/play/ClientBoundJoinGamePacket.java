package me.dustin.chatbot.network.packet.s2c.play;

import me.dustin.chatbot.nbt.NbtCompound;
import me.dustin.chatbot.network.packet.ProtocolHandler;
import me.dustin.chatbot.network.packet.pipeline.PacketByteBuf;
import me.dustin.chatbot.network.packet.Packet;
import me.dustin.chatbot.network.packet.handler.ClientBoundPacketHandler;
import me.dustin.chatbot.network.packet.handler.PlayClientBoundPacketHandler;
import me.dustin.chatbot.entity.player.PlayerInfo;
import me.dustin.chatbot.world.World;

public class ClientBoundJoinGamePacket extends Packet.ClientBoundPacket {
    private final int entityId;
    private final boolean isHardcore;
    private final PlayerInfo.GameMode gameMode;
    private final PlayerInfo.GameMode previousGameMode;
    private final String[] dimNames;
    private final NbtCompound dimensionCodec;
    private final NbtCompound dimensionNBT;
    private final World.Dimension dimension;
    private final long hashedSeed;
    private final int maxPlayers;
    private final int viewDistance;
    private final int simulationDistance;
    private final boolean reducedDebugInfo;
    private final boolean enabledRespawnScreen;
    private final boolean isDebug;
    private final boolean isFlat;

    private World.Difficulty difficulty;

    public ClientBoundJoinGamePacket(PacketByteBuf packetByteBuf) {
        super(packetByteBuf);
        this.entityId = packetByteBuf.readInt();
        if (ProtocolHandler.getCurrent().getProtocolVer() <= ProtocolHandler.getVersionFromName("1.15.2").getProtocolVer()) {
            this.gameMode = PlayerInfo.GameMode.get(packetByteBuf.readByte());
            this.dimension = World.Dimension.get(ProtocolHandler.getCurrent().getProtocolVer() < ProtocolHandler.getVersionFromName("1.10.2").getProtocolVer() ? packetByteBuf.readByte() : packetByteBuf.readInt());
            if (ProtocolHandler.getCurrent().getProtocolVer() >= ProtocolHandler.getVersionFromName("1.14").getProtocolVer())
                this.hashedSeed = packetByteBuf.readLong();
            else
                this.hashedSeed = -1;
            if (ProtocolHandler.getCurrent().getProtocolVer() <= ProtocolHandler.getVersionFromName("1.13").getProtocolVer())
                this.difficulty = World.Difficulty.values()[packetByteBuf.readByte()];//difficulty on 1.13 or below
            this.maxPlayers = packetByteBuf.readByte();
            this.isFlat = packetByteBuf.readString().contains("flat");
            if (ProtocolHandler.getCurrent().getProtocolVer() > ProtocolHandler.getVersionFromName("1.13.2").getProtocolVer()) {
                this.viewDistance = packetByteBuf.readVarInt();
                this.simulationDistance = this.viewDistance - 1;
            } else {
                this.viewDistance = 8;
                this.simulationDistance = 7;
            }
            if (ProtocolHandler.getCurrent().getProtocolVer() > ProtocolHandler.getVersionFromName("1.8.9").getProtocolVer())
                this.reducedDebugInfo = packetByteBuf.readBoolean();
            else
                this.reducedDebugInfo = false;
            if (ProtocolHandler.getCurrent().getProtocolVer() >= ProtocolHandler.getVersionFromName("1.14").getProtocolVer())
                this.enabledRespawnScreen = packetByteBuf.readBoolean();
            else
                this.enabledRespawnScreen = true;
            this.isDebug = false;
            this.isHardcore = false;
            this.previousGameMode = this.gameMode;
            this.dimNames = new String[0];
            this.dimensionCodec = new NbtCompound();
            this.dimensionNBT = new NbtCompound();
            return;
        }

        this.isHardcore = packetByteBuf.readBoolean();
        this.gameMode = PlayerInfo.GameMode.get(packetByteBuf.readByte());
        this.previousGameMode = PlayerInfo.GameMode.get(packetByteBuf.readByte());
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
    public void apply(ClientBoundPacketHandler clientBoundPacketHandler) {
        ((PlayClientBoundPacketHandler)clientBoundPacketHandler).handleJoinGamePacket(this);
    }

    public int getEntityId() {
        return entityId;
    }

    public boolean isHardcore() {
        return isHardcore;
    }

    public PlayerInfo.GameMode getGameMode() {
        return gameMode;
    }

    public PlayerInfo.GameMode getPreviousGameMode() {
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
