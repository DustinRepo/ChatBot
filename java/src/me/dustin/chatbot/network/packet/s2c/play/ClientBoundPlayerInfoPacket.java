package me.dustin.chatbot.network.packet.s2c.play;

import me.dustin.chatbot.helper.MCAPIHelper;
import me.dustin.chatbot.network.packet.ProtocolHandler;
import me.dustin.chatbot.network.packet.pipeline.PacketByteBuf;
import me.dustin.chatbot.network.packet.Packet;
import me.dustin.chatbot.network.packet.handler.PlayClientBoundPacketHandler;
import me.dustin.chatbot.network.packet.handler.ClientBoundPacketHandler;
import me.dustin.chatbot.network.player.OtherPlayer;

import java.util.ArrayList;
import java.util.UUID;

public class ClientBoundPlayerInfoPacket extends Packet.ClientBoundPacket {

    public static final int ADD_PLAYER = 0, UPDATE_GAMEMODE = 1, UPDATE_PING = 2, UPDATE_DISPLAY_NAME = 3, REMOVE_PLAYER = 4;

    private final int action;
    private final OtherPlayer[] players;

    public ClientBoundPlayerInfoPacket(PacketByteBuf packetByteBuf) {
        super(packetByteBuf);
        //1.7 versions of this packet are very different
        if (ProtocolHandler.getCurrent().getProtocolVer() <= ProtocolHandler.getVersionFromName("1.7.10").getProtocolVer()) {
            String playerName = packetByteBuf.readString();
            boolean online = packetByteBuf.readBoolean();
            action = online ? ADD_PLAYER : REMOVE_PLAYER;
            int ping = packetByteBuf.readShort();

            players = new OtherPlayer[1];
            players[0] = new OtherPlayer(playerName, MCAPIHelper.getUUIDFromName(playerName));
            players[0].setPing(ping);
            return;
        }

        this.action = packetByteBuf.readVarInt();
        int playerNumbers = packetByteBuf.readVarInt();
        this.players = new OtherPlayer[playerNumbers];

        for (int i = 0; i < playerNumbers; i++) {
            UUID uuid = packetByteBuf.readUuid();
            OtherPlayer player = getClientConnection().getPlayerManager().get(uuid);
            if (player == null)
                player = new OtherPlayer("", uuid);
            switch (this.action) {
                case ADD_PLAYER -> {
                    player.setName(packetByteBuf.readString());
                    int propertyListSize = packetByteBuf.readVarInt();
                    ArrayList<OtherPlayer.PlayerProperty> properties = new ArrayList<>();
                    for (int ii = 0; ii < propertyListSize; ii++) {
                        String pName = packetByteBuf.readString();
                        String pValue = packetByteBuf.readString();
                        boolean isSigned = packetByteBuf.readBoolean();
                        String signature = "";
                        if (isSigned) {
                            signature = packetByteBuf.readString();
                        }
                        properties.add(new OtherPlayer.PlayerProperty(pName, pValue, isSigned, signature));
                    }
                    player.getProperties().addAll(properties);
                    player.setGameMode(OtherPlayer.GameMode.get(packetByteBuf.readVarInt()));
                    player.setPing(packetByteBuf.readVarInt());
                    boolean hasDisplayName = packetByteBuf.readBoolean();
                    if (hasDisplayName) {
                        player.setDisplayName(packetByteBuf.readString());
                    }
                }
                case UPDATE_GAMEMODE -> {
                    player.setGameMode(OtherPlayer.GameMode.get(packetByteBuf.readVarInt()));
                }
                case UPDATE_PING -> {
                    player.setPing(packetByteBuf.readVarInt());
                }
                case UPDATE_DISPLAY_NAME -> {
                    boolean hasDisplayName = packetByteBuf.readBoolean();
                    if (hasDisplayName)
                        player.setDisplayName(packetByteBuf.readString());
                    else
                        player.setDisplayName(player.getName());
                }
                //we don't do anything for remove player because that's done in ClientBoundPlayClientBoundPacketHandler
            }
            this.players[i] = player;
        }
    }

    @Override
    public void apply(ClientBoundPacketHandler clientBoundPacketHandler) {
        ((PlayClientBoundPacketHandler)clientBoundPacketHandler).handlePlayerInfoPacket(this);
    }

    public int getAction() {
        return action;
    }

    public OtherPlayer[] getPlayers() {
        return players;
    }
}
