package me.dustin.chatbot.network.packet.s2c.play;

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
    private byte previousGameMode;
    private String[] dimNames;
    private String dimensionCodec;
    private String dimensionNBT;
    private String dimensionName;
    private long hashedSeed;
    private int maxPlayers;
    private int viewDistance;
    private int simulationDistance;
    private boolean reducedDebugInfo;
    private boolean enabledRespawnScreen;
    private boolean isDebug;
    private boolean isFlat;

    public ClientBoundJoinGamePacket(ClientBoundPacketHandler clientBoundPacketHandler) {
        super(clientBoundPacketHandler);
    }

    @Override
    public void createPacket(PacketByteBuf packetByteBuf) throws IOException {

        this.entityId = packetByteBuf.readInt();
        //this works on ~1.16 and above, but it's really not needed and has been massively changed throughout the versions
        //I really only need this packet for confirmation that the player has joined

        /*this.isHardcore = dataInputStream.readBoolean();
        this.gameMode = OtherPlayer.GameMode.get(dataInputStream.readByte());
        this.previousGameMode = dataInputStream.readByte();

        int worldCount = readVarInt(dataInputStream);
        dimNames = new String[worldCount];
        for (int i = 0; i < worldCount; i++) {
            dimNames[i] = readString(dataInputStream);
        }
        dimensionCodec = readString(dataInputStream);
        dimensionNBT = readString(dataInputStream);
        dimensionName = readString(dataInputStream);
        hashedSeed = dataInputStream.readLong();
        maxPlayers = readVarInt(dataInputStream);
        viewDistance = readVarInt(dataInputStream);
        if (ChatBot.getConfig().getProtocolVersion() >= Protocols.V1_18.getProtocolVer())
            simulationDistance = readVarInt(dataInputStream);
        reducedDebugInfo = dataInputStream.readBoolean();
        enabledRespawnScreen = dataInputStream.readBoolean();
        if (ChatBot.getConfig().getProtocolVersion() >= Protocols.V1_15_2.getProtocolVer()) {
            isDebug = dataInputStream.readBoolean();
            isFlat = dataInputStream.readBoolean();
        }*/
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

    public byte getPreviousGameMode() {
        return previousGameMode;
    }

    public String[] getDimNames() {
        return dimNames;
    }

    public String getDimensionCodec() {
        return dimensionCodec;
    }

    public String getDimensionNBT() {
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
}
