package me.dustin.chatbot.network.packet.s2c.play;

import me.dustin.chatbot.network.packet.Packet;
import me.dustin.chatbot.network.packet.handler.ClientBoundPlayClientBoundPacketHandler;
import me.dustin.chatbot.network.packet.handler.ClientBoundPacketHandler;
import me.dustin.chatbot.network.player.OtherPlayer;
import me.dustin.chatbot.network.player.PlayerManager;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.UUID;

public class ClientBoundPlayerInfoPacket extends Packet.ClientBoundPacket {

    public static final int ADD_PLAYER = 1, UPDATE_GAMEMODE = 0, UPDATE_PING = 2, UPDATE_DISPLAY_NAME = 3, REMOVE_PLAYER = 4;

    private int action;

    private UUID uuid;

    private OtherPlayer[] players;

    public ClientBoundPlayerInfoPacket(ClientBoundPacketHandler clientBoundPacketHandler) {
        super(clientBoundPacketHandler);
    }

    @Override
    public void createPacket(ByteArrayInputStream byteArrayInputStream) throws IOException {
        DataInputStream dataInputStream = new DataInputStream(byteArrayInputStream);
        action = readVarInt(dataInputStream);
        int playerNumbers = readVarInt(dataInputStream);
        players = new OtherPlayer[playerNumbers];
        for (int i = 0; i < playerNumbers; i++) {
            uuid = readUUID(dataInputStream);
            OtherPlayer player = PlayerManager.INSTANCE.get(uuid);
            if (player == null)
                player = new OtherPlayer("", uuid);
            players[i] = player;
            switch (action) {
                case ADD_PLAYER -> {
                    player.name = readString(dataInputStream);
                    int propertyListSize = readVarInt(dataInputStream);
                    String pName = null;
                    String pValue = null;
                    String signature = null;
                    if (propertyListSize > 0) {
                        pName = readString(dataInputStream);
                    }
                    if (propertyListSize > 1) {
                        pValue = readString(dataInputStream);
                    }
                    if (propertyListSize > 2) {
                        boolean isSigned = dataInputStream.readBoolean();
                        if (isSigned) {
                            signature = readString(dataInputStream);
                        }
                    }
                    player.properties = new OtherPlayer.PlayerProperty(pName, pValue, signature);
                    player.gameMode = readVarInt(dataInputStream);
                    player.ping = readVarInt(dataInputStream);
                    boolean hasDisplayName = dataInputStream.readBoolean();
                    if (hasDisplayName) {
                        player.displayName = readString(dataInputStream);
                    }
                }
                case UPDATE_PING -> {
                    player.ping = readVarInt(dataInputStream);
                }
            }
        }
        super.createPacket(byteArrayInputStream);
    }

    @Override
    public void apply() {
        ((ClientBoundPlayClientBoundPacketHandler)clientBoundPacketHandler).handlePlayerInfoPacket(this);
    }

    public int getAction() {
        return action;
    }

    public UUID getUuid() {
        return uuid;
    }

    public OtherPlayer[] getPlayers() {
        return players;
    }
}
