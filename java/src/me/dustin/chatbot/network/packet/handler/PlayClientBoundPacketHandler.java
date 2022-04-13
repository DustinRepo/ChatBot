package me.dustin.chatbot.network.packet.handler;

import io.netty.buffer.Unpooled;
import me.dustin.chatbot.chat.ChatMessage;
import me.dustin.chatbot.event.EventAddPlayer;
import me.dustin.chatbot.event.EventReceiveChatMessage;
import me.dustin.chatbot.event.EventReceiveTabComplete;
import me.dustin.chatbot.event.EventRemovePlayer;
import me.dustin.chatbot.helper.GeneralHelper;
import me.dustin.chatbot.network.packet.Packet;
import me.dustin.chatbot.network.packet.ProtocolHandler;
import me.dustin.chatbot.network.packet.c2s.play.*;
import me.dustin.chatbot.network.packet.pipeline.PacketByteBuf;
import me.dustin.chatbot.network.packet.s2c.play.*;
import me.dustin.chatbot.network.player.ClientPlayer;
import me.dustin.chatbot.network.player.OtherPlayer;
import me.dustin.chatbot.network.world.World;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class PlayClientBoundPacketHandler extends ClientBoundPacketHandler {

    public void handleDisconnectPacket(ClientBoundDisconnectPlayPacket clientBoundDisconnectPacket) {
        GeneralHelper.print("Disconnected", ChatMessage.TextColor.DARK_RED);
        GeneralHelper.printChat(ChatMessage.of(clientBoundDisconnectPacket.getReason()));
    }

    public void handleKeepAlivePacket(ClientBoundKeepAlivePacket keepAlivePacket) {
        getClientConnection().sendPacket(new ServerBoundKeepAlivePacket(keepAlivePacket.getId()));
    }

    public void handleChatMessagePacket(ClientBoundChatMessagePacket clientBoundChatMessagePacket) {
        new EventReceiveChatMessage(clientBoundChatMessagePacket).run(getClientConnection());
        GeneralHelper.printChat(clientBoundChatMessagePacket.getMessage());
    }

    public void handleJoinGamePacket(ClientBoundJoinGamePacket clientBoundJoinGamePacket) {
        getClientConnection().getClientPlayer().setEntityId(clientBoundJoinGamePacket.getEntityId());
        getClientConnection().getEventManager().run(clientBoundJoinGamePacket);

        //setup stuff from packet
        ClientPlayer clientPlayer = getClientConnection().getClientPlayer();
        World world = getClientConnection().getWorld();

        world.setDimension(clientBoundJoinGamePacket.getDimension());
        if (clientBoundJoinGamePacket.getDifficulty() != null)
            world.setDifficulty(clientBoundJoinGamePacket.getDifficulty());
        clientPlayer.setGameMode(clientBoundJoinGamePacket.getGameMode());

        //send brand data
        String channel = "minecraft:brand";
        if (ProtocolHandler.getCurrent().getProtocolVer() <= ProtocolHandler.getVersionFromName("1.12.2").getProtocolVer())
            channel = "MC|Brand";
        getClientConnection().sendPacket(new ServerBoundCustomDataPacket(channel, new PacketByteBuf(Unpooled.buffer()).writeString("vanilla")));
    }

    public void handleTabComplete(ClientBoundTabCompletePacket clientBoundTabCompletePacket) {
        new EventReceiveTabComplete(clientBoundTabCompletePacket).run(getClientConnection());
    }

    public void handleResourcePackPacket(ClientBoundResourcePackSendPacket clientBoundResourcePackSendPacket) {
        if (clientBoundResourcePackSendPacket.isForced()) {
            //tell the server we have the resource pack if it forces one
            getClientConnection().sendPacket(new ServerBoundResourcePackStatusPacket(ServerBoundResourcePackStatusPacket.ACCEPTED));
            getClientConnection().sendPacket(new ServerBoundResourcePackStatusPacket(ServerBoundResourcePackStatusPacket.SUCCESSFULLY_LOADED));
        }
    }

    public void handlePlayerInfoPacket(ClientBoundPlayerInfoPacket clientBoundPlayerInfoPacket) {
        for (OtherPlayer player : clientBoundPlayerInfoPacket.getPlayers()) {
            switch (clientBoundPlayerInfoPacket.getAction()) {
                case ClientBoundPlayerInfoPacket.ADD_PLAYER -> {
                    if (getClientConnection().getPlayerManager().get(player.getName()) == null) {
                        getClientConnection().getPlayerManager().getPlayers().add(player);
                        new EventAddPlayer(player).run(getClientConnection());
                    }
                }
                case ClientBoundPlayerInfoPacket.REMOVE_PLAYER -> {
                    if (player != null) {
                        getClientConnection().getPlayerManager().getPlayers().remove(player);
                        new EventRemovePlayer(player).run(getClientConnection());
                    }
                }
                case ClientBoundPlayerInfoPacket.UPDATE_GAMEMODE -> {
                    if (player != null) {
                        UUID uuid = player.getUuid();
                        if (GeneralHelper.matchUUIDs(getClientConnection().getSession().getUuid(), uuid.toString())) {
                            getClientConnection().getClientPlayer().setGameMode(player.getGameMode());
                        }
                    }
                }
            }
        }
    }

    public void handleServerDifficultyPacket(ClientBoundServerDifficultyPacket clientBoundServerDifficultyPacket) {
        getClientConnection().getWorld().setDifficulty(clientBoundServerDifficultyPacket.getDifficulty());
    }

    public void handlePingPacket(ClientBoundPingPacket clientBoundPingPacket) {
        getClientConnection().sendPacket(new ServerBoundPongPacket(clientBoundPingPacket.getPacketId()));
    }

    public void handleWorldTimePacket(ClientBoundWorldTimePacket clientBoundWorldTimePacket) {
        getClientConnection().getTpsHelper().worldTime();
    }

    public void handleUpdateHealthPacket(ClientBoundUpdateHealthPacket clientBoundUpdateHealthPacket) {
        if (clientBoundUpdateHealthPacket.getHealth() <= 0) {
            getClientConnection().sendPacket(new ServerBoundClientStatusPacket(ServerBoundClientStatusPacket.RESPAWN));
        }
    }

    public void handleCustomDataPacket(ClientBoundCustomDataPacket clientBoundCustomDataPacket) {
    }

    public void handlePlayerPositionAndLookPacket(ClientBoundPlayerPositionAndLookPacket clientBoundPlayerPositionAndLookPacket) {
        byte flags = clientBoundPlayerPositionAndLookPacket.getFlags();
        boolean xRelative = (flags & 0x01) == 0x01;
        boolean yRelative = (flags & 0x02) == 0x02;
        boolean zRelative = (flags & 0x04) == 0x04;
        boolean yawRelative = (flags & 0x08) == 0x08;
        boolean pitchRelative = (flags & 0x10) == 0x10;

        ClientPlayer clientPlayer = getClientConnection().getClientPlayer();

        if (xRelative)
            clientPlayer.moveX(clientBoundPlayerPositionAndLookPacket.getX());
        else
            clientPlayer.setX(clientBoundPlayerPositionAndLookPacket.getX());

        if (yRelative)
            clientPlayer.moveY(clientBoundPlayerPositionAndLookPacket.getY());
        else
            clientPlayer.setY(clientBoundPlayerPositionAndLookPacket.getY());

        if (zRelative)
            clientPlayer.moveZ(clientBoundPlayerPositionAndLookPacket.getZ());
        else
            clientPlayer.setZ(clientBoundPlayerPositionAndLookPacket.getZ());

        if (yawRelative)
            clientPlayer.moveYaw(clientBoundPlayerPositionAndLookPacket.getYaw());
        else
            clientPlayer.setYaw(clientBoundPlayerPositionAndLookPacket.getYaw());

        if (pitchRelative)
            clientPlayer.movePitch(clientBoundPlayerPositionAndLookPacket.getPitch());
        else
            clientPlayer.setPitch(clientBoundPlayerPositionAndLookPacket.getPitch());

        Packet packet = new ServerBoundConfirmTeleportPacket(clientBoundPlayerPositionAndLookPacket.getTeleportId());
        if (packet.getPacketId() == -1)
            packet = new ServerBoundPlayerPositionAndRotationPacket(clientPlayer.getX(), clientPlayer.getY(), clientPlayer.getZ(), clientPlayer.getYaw(), clientPlayer.getPitch(), true);
        getClientConnection().sendPacket(packet);
    }
}
