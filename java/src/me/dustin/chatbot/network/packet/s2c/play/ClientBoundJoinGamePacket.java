package me.dustin.chatbot.network.packet.s2c.play;

import me.dustin.chatbot.nbt.NbtCompound;
import me.dustin.chatbot.network.packet.ProtocolHandler;
import me.dustin.chatbot.network.packet.pipeline.PacketByteBuf;
import me.dustin.chatbot.network.packet.Packet;
import me.dustin.chatbot.network.packet.handler.ClientBoundPacketHandler;
import me.dustin.chatbot.network.packet.handler.PlayClientBoundPacketHandler;
import me.dustin.chatbot.network.player.OtherPlayer;

import java.io.IOException;

public class ClientBoundJoinGamePacket extends Packet.ClientBoundPacket {
    private int entityId;
    private boolean isHardcore;
    private OtherPlayer.GameMode gameMode;
    private OtherPlayer.GameMode previousGameMode;
    private String[] dimNames;
    private NbtCompound dimensionCodec;
    private NbtCompound dimensionNBT;
    private String dimensionName;
    private long hashedSeed;
    private int maxPlayers;
    private int viewDistance;
    private int simulationDistance;
    private boolean reducedDebugInfo;
    private boolean enabledRespawnScreen = true;
    private boolean isDebug;
    private boolean isFlat;

    public ClientBoundJoinGamePacket(ClientBoundPacketHandler clientBoundPacketHandler) {
        super(clientBoundPacketHandler);
    }

    @Override
    public void createPacket(PacketByteBuf packetByteBuf) throws IOException {
        this.entityId = packetByteBuf.readInt();

        if (ProtocolHandler.getCurrent().getProtocolVer() <= ProtocolHandler.getVersionFromName("1.15.2").getProtocolVer()) {
            this.gameMode = OtherPlayer.GameMode.get(packetByteBuf.readByte());
            this.dimensionName = getDimName(ProtocolHandler.getCurrent().getProtocolVer() < ProtocolHandler.getVersionFromName("1.10.2").getProtocolVer() ? packetByteBuf.readByte() : packetByteBuf.readInt());
            if (ProtocolHandler.getCurrent().getProtocolVer() >= ProtocolHandler.getVersionFromName("1.14").getProtocolVer())
                this.hashedSeed = packetByteBuf.readLong();
            if (ProtocolHandler.getCurrent().getProtocolVer() <= ProtocolHandler.getVersionFromName("1.13").getProtocolVer())
                packetByteBuf.readByte();//difficulty on 1.13 or below
            this.maxPlayers = packetByteBuf.readByte();
            this.isFlat = packetByteBuf.readString().contains("flat");
            if (ProtocolHandler.getCurrent().getProtocolVer() > ProtocolHandler.getVersionFromName("1.13.2").getProtocolVer()) {
                this.viewDistance = packetByteBuf.readVarInt();
                this.simulationDistance = this.viewDistance;
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
        dimNames = new String[worldCount];
        for (int i = 0; i < worldCount; i++) {
            dimNames[i] = packetByteBuf.readString();
        }
        dimensionCodec = (NbtCompound) packetByteBuf.readNbt();
        dimensionNBT = (NbtCompound) packetByteBuf.readNbt();
        dimensionName = packetByteBuf.readString();
        hashedSeed = packetByteBuf.readLong();
        maxPlayers = packetByteBuf.readVarInt();
        viewDistance = packetByteBuf.readVarInt();
        if (ProtocolHandler.getCurrent().getProtocolVer() >= ProtocolHandler.getVersionFromName("1.18.1").getProtocolVer())
            simulationDistance = packetByteBuf.readVarInt();
        else
            simulationDistance = viewDistance;
        reducedDebugInfo = packetByteBuf.readBoolean();
        enabledRespawnScreen = packetByteBuf.readBoolean();
        isDebug = packetByteBuf.readBoolean();
        isFlat = packetByteBuf.readBoolean();
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

    public String getDimensionName() {
        return dimensionName;
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

    public String getDimName(int dim) {
        switch (dim) {
            case -1 -> {
                return "minecraft:nether";
            }
            case 0 -> {
                return "minecraft:overworld";
            }
            case 1 -> {
                return "minecraft:end";
            }
        }
        return "minecraft:overworld";
    }
}
