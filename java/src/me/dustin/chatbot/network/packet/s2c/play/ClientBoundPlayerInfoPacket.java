package me.dustin.chatbot.network.packet.s2c.play;

import me.dustin.chatbot.ChatBot;
import me.dustin.chatbot.helper.GeneralHelper;
import me.dustin.chatbot.helper.MCAPIHelper;
import me.dustin.chatbot.network.Protocols;
import me.dustin.chatbot.network.packet.Packet;
import me.dustin.chatbot.network.packet.handler.ClientBoundPlayClientBoundPacketHandler;
import me.dustin.chatbot.network.packet.handler.ClientBoundPacketHandler;
import me.dustin.chatbot.network.player.OtherPlayer;
import me.dustin.chatbot.network.player.PlayerManager;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

public class ClientBoundPlayerInfoPacket extends Packet.ClientBoundPacket {

    public static final int ADD_PLAYER = 0, UPDATE_GAMEMODE = 1, UPDATE_PING = 2, UPDATE_DISPLAY_NAME = 3, REMOVE_PLAYER = 4;

    private int action;

    private OtherPlayer[] players;

    public ClientBoundPlayerInfoPacket(ClientBoundPacketHandler clientBoundPacketHandler) {
        super(clientBoundPacketHandler);
    }

    @Override
    public void createPacket(DataInputStream dataInputStream) throws IOException {
        //1.7 versions of this packet are very different
        if (ChatBot.getConfig().getProtocolVersion() <= Protocols.V1_7_10.getProtocolVer()) {
            String playerName = readString(dataInputStream);
            boolean online = dataInputStream.readBoolean();
            int ping = dataInputStream.readShort();

            players = new OtherPlayer[1];
            players[0] = new OtherPlayer(playerName, MCAPIHelper.getUUIDFromName(playerName));
            players[0].setPing(ping);
            return;
        }

        this.action = readVarInt(dataInputStream);
        int playerNumbers = readVarInt(dataInputStream);
        this.players = new OtherPlayer[playerNumbers];

        for (int i = 0; i < playerNumbers; i++) {
            UUID uuid = readUUID(dataInputStream);
            OtherPlayer player = getClientConnection().getPlayerManager().get(uuid);
            if (player == null)
                player = new OtherPlayer("", uuid);
            switch (this.action) {
                case ADD_PLAYER -> {
                    player.setName(readString(dataInputStream));
                    int propertyListSize = readVarInt(dataInputStream);
                    ArrayList<OtherPlayer.PlayerProperty> properties = new ArrayList<>();
                    for (int ii = 0; ii < propertyListSize; ii++) {
                        String pName = readString(dataInputStream);
                        String pValue = readString(dataInputStream);
                        boolean isSigned = dataInputStream.readBoolean();
                        String signature = "";
                        if (isSigned) {
                            signature = readString(dataInputStream);
                        }
                        properties.add(new OtherPlayer.PlayerProperty(pName, pValue, isSigned, signature));
                    }
                    player.getProperties().addAll(properties);
                    player.setGameMode(OtherPlayer.GameMode.get(readVarInt(dataInputStream)));
                    player.setPing(readVarInt(dataInputStream));
                    boolean hasDisplayName = dataInputStream.readBoolean();
                    if (hasDisplayName) {
                        player.setDisplayName(readString(dataInputStream));
                    }
                }
                case UPDATE_GAMEMODE -> {
                    player.setGameMode(OtherPlayer.GameMode.get(readVarInt(dataInputStream)));
                }
                case UPDATE_PING -> {
                    player.setPing(readVarInt(dataInputStream));
                }
                case UPDATE_DISPLAY_NAME -> {
                    boolean hasDisplayName = dataInputStream.readBoolean();
                    if (hasDisplayName)
                        player.setDisplayName(readString(dataInputStream));
                    else
                        player.setDisplayName(player.getName());
                }
                //we don't do anything for remove player because that's done in ClientBoundPlayClientBoundPacketHandler
            }
            this.players[i] = player;
        }
    }

    @Override
    public void apply() {
        ((ClientBoundPlayClientBoundPacketHandler)clientBoundPacketHandler).handlePlayerInfoPacket(this);
    }

    public int getAction() {
        return action;
    }

    public OtherPlayer[] getPlayers() {
        return players;
    }
}
