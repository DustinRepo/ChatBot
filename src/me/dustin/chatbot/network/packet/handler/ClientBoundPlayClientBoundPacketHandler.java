package me.dustin.chatbot.network.packet.handler;

import me.dustin.chatbot.ChatBot;
import me.dustin.chatbot.chat.ChatMessage;
import me.dustin.chatbot.command.impl.CommandPlugins;
import me.dustin.chatbot.event.EventAddPlayer;
import me.dustin.chatbot.event.EventReceiveChatMessage;
import me.dustin.chatbot.event.EventReceiveTabComplete;
import me.dustin.chatbot.event.EventRemovePlayer;
import me.dustin.chatbot.helper.GeneralHelper;
import me.dustin.chatbot.network.ClientConnection;
import me.dustin.chatbot.network.packet.c2s.play.*;
import me.dustin.chatbot.network.packet.s2c.play.*;
import me.dustin.chatbot.network.player.ClientPlayer;
import me.dustin.chatbot.network.player.OtherPlayer;

import java.util.UUID;

public class ClientBoundPlayClientBoundPacketHandler extends ClientBoundPacketHandler {

    public ClientBoundPlayClientBoundPacketHandler(ClientConnection clientConnection) {
        super(clientConnection);

        int protocolVersion = ChatBot.getConfig().getProtocolVersion();
        //https://wiki.vg/Protocol_version_numbers - select the "page" part and it will show all packets, their IDs, and how they're constructed
        if (protocolVersion >= 755) {//all 1.17 - 1.18.2 compatible packets
            getPacketMap().put(0x1A, ClientBoundDisconnectPlayPacket.class);
            getPacketMap().put(0x0F, ClientBoundChatMessagePacket.class);
            getPacketMap().put(0x21, ClientBoundKeepAlivePacket.class);
            getPacketMap().put(0x35, ClientBoundPlayerDeadPacket.class);
            getPacketMap().put(0x36, ClientBoundPlayerInfoPacket.class);
            getPacketMap().put(0x38, ClientBoundPlayerPositionAndLookPacket.class);
            getPacketMap().put(0x3C, ClientBoundResourcePackSendPacket.class);
            getPacketMap().put(0x11, ClientBoundTabCompletePacket.class);
            getPacketMap().put(0x52, ClientBoundUpdateHealthPacket.class);
            getPacketMap().put(0x59, ClientBoundWorldTimePacket.class);
        } else if (protocolVersion >= 751){//1.16.2 - 1.16.5
            getPacketMap().put(0x1F, ClientBoundKeepAlivePacket.class);
            getPacketMap().put(0x19, ClientBoundDisconnectPlayPacket.class);
            getPacketMap().put(0x0E, ClientBoundChatMessagePacket.class);
            getPacketMap().put(0x31, ClientBoundPlayerDeadPacket.class);
            getPacketMap().put(0x32, ClientBoundPlayerInfoPacket.class);
            getPacketMap().put(0x34, ClientBoundPlayerPositionAndLookPacket.class);
            getPacketMap().put(0x38, ClientBoundResourcePackSendPacket.class);
            getPacketMap().put(0xF, ClientBoundTabCompletePacket.class);
            getPacketMap().put(0x49, ClientBoundUpdateHealthPacket.class);
            getPacketMap().put(0x4E, ClientBoundWorldTimePacket.class);
        } else if (protocolVersion >= 735) {//1.16 and 1.16.1
            getPacketMap().put(0x20, ClientBoundKeepAlivePacket.class);
            getPacketMap().put(0x1A, ClientBoundDisconnectPlayPacket.class);
            getPacketMap().put(0xE, ClientBoundChatMessagePacket.class);
            getPacketMap().put(0x32, ClientBoundPlayerDeadPacket.class);
            getPacketMap().put(0x33, ClientBoundPlayerInfoPacket.class);
            getPacketMap().put(0x35, ClientBoundPlayerPositionAndLookPacket.class);
            getPacketMap().put(0x39, ClientBoundResourcePackSendPacket.class);
            getPacketMap().put(0x10, ClientBoundTabCompletePacket.class);
            getPacketMap().put(0x49, ClientBoundUpdateHealthPacket.class);
            getPacketMap().put(0x4E, ClientBoundWorldTimePacket.class);
        } else if (protocolVersion >= 573) {//1.15 - 1.15.2
            getPacketMap().put(0x21, ClientBoundKeepAlivePacket.class);
            getPacketMap().put(0x1B, ClientBoundDisconnectPlayPacket.class);
            getPacketMap().put(0xF, ClientBoundChatMessagePacket.class);
            getPacketMap().put(0x33, ClientBoundPlayerDeadPacket.class);
            getPacketMap().put(0x34, ClientBoundPlayerInfoPacket.class);
            getPacketMap().put(0x36, ClientBoundPlayerPositionAndLookPacket.class);
            getPacketMap().put(0x3A, ClientBoundResourcePackSendPacket.class);
            getPacketMap().put(0x11, ClientBoundTabCompletePacket.class);
            getPacketMap().put(0x49, ClientBoundUpdateHealthPacket.class);
            getPacketMap().put(0x4F, ClientBoundWorldTimePacket.class);
        } else if (protocolVersion >= 477) {//1.14 - 1.14.4
            getPacketMap().put(0x20, ClientBoundKeepAlivePacket.class);
            getPacketMap().put(0x1A, ClientBoundDisconnectPlayPacket.class);
            getPacketMap().put(0xE, ClientBoundChatMessagePacket.class);
            getPacketMap().put(0x32, ClientBoundPlayerDeadPacket.class);
            getPacketMap().put(0x33, ClientBoundPlayerInfoPacket.class);
            getPacketMap().put(0x35, ClientBoundPlayerPositionAndLookPacket.class);
            getPacketMap().put(0x39, ClientBoundResourcePackSendPacket.class);
            getPacketMap().put(0x10, ClientBoundTabCompletePacket.class);
            getPacketMap().put(0x48, ClientBoundUpdateHealthPacket.class);//only difference in IDs between this and 1.16
            getPacketMap().put(0x4E, ClientBoundWorldTimePacket.class);
        } else if (protocolVersion >= 393) {//1.13 - 1.13.2
            getPacketMap().put(0x21, ClientBoundKeepAlivePacket.class);
            getPacketMap().put(0x1B, ClientBoundDisconnectPlayPacket.class);
            getPacketMap().put(0x0E, ClientBoundChatMessagePacket.class);
            getPacketMap().put(0x2F, ClientBoundPlayerDeadPacket.class);
            getPacketMap().put(0x30, ClientBoundPlayerInfoPacket.class);
            getPacketMap().put(0x32, ClientBoundPlayerPositionAndLookPacket.class);
            getPacketMap().put(0x37, ClientBoundResourcePackSendPacket.class);
            getPacketMap().put(0x10, ClientBoundTabCompletePacket.class);
            getPacketMap().put(0x44, ClientBoundUpdateHealthPacket.class);
            getPacketMap().put(0x4A, ClientBoundWorldTimePacket.class);
        } if (ChatBot.getConfig().getProtocolVersion() >= 335) {//1.12 - 1.12.2
            getPacketMap().put(0x1F, ClientBoundKeepAlivePacket.class);
            getPacketMap().put(0x1A, ClientBoundDisconnectPlayPacket.class);
            getPacketMap().put(0x0F, ClientBoundChatMessagePacket.class);
            getPacketMap().put(0x2D, ClientBoundPlayerDeadPacket.class);
            getPacketMap().put(0x2E, ClientBoundPlayerInfoPacket.class);
            getPacketMap().put(0x2F, ClientBoundPlayerPositionAndLookPacket.class);
            getPacketMap().put(0x34, ClientBoundResourcePackSendPacket.class);
            getPacketMap().put(0xe, ClientBoundTabCompletePacket.class);
            getPacketMap().put(0x41, ClientBoundUpdateHealthPacket.class);
            getPacketMap().put(0x47, ClientBoundWorldTimePacket.class);
        }
    }

    public void handleDisconnectPacket(ClientBoundDisconnectPlayPacket clientBoundDisconnectPacket) {
        GeneralHelper.printChat(ChatMessage.of(clientBoundDisconnectPacket.getReason()));
        getClientConnection().close();
    }

    public void handleKeepAlivePacket(ClientBoundKeepAlivePacket keepAlivePacket) {
        //send KeepAlive packet back with same ID
        long id = keepAlivePacket.getId();
        getClientConnection().sendPacket(new ServerBoundKeepAlivePacket(id));
    }

    public void handleChatMessagePacket(ClientBoundChatMessagePacket clientBoundChatMessagePacket) {
        new EventReceiveChatMessage(clientBoundChatMessagePacket).run(getClientConnection());
        GeneralHelper.printChat(clientBoundChatMessagePacket.getMessage());
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
                    if (!getClientConnection().getPlayerManager().getPlayers().contains(player)) {
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
            }
        }
    }

    public void handleWorldTimePacket(ClientBoundWorldTimePacket clientBoundWorldTimePacket) {
        getClientConnection().getTpsHelper().worldTime();
    }

    public void handleUpdateHealthPacket(ClientBoundUpdateHealthPacket clientBoundUpdateHealthPacket) {
        if (clientBoundUpdateHealthPacket.getHealth() <= 0) {
            getClientConnection().sendPacket(new ServerBoundClientStatusPacket(ServerBoundClientStatusPacket.RESPAWN));
        }
    }

    public void handlePlayerDeadPacket(ClientBoundPlayerDeadPacket clientBoundPlayerDeadPacket) {
        if (clientBoundPlayerDeadPacket.getType() == ClientBoundPlayerDeadPacket.ENTITY_DIED || !clientBoundPlayerDeadPacket.getMessage().isEmpty())
            getClientConnection().sendPacket(new ServerBoundClientStatusPacket(ServerBoundClientStatusPacket.RESPAWN));
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

        getClientConnection().sendPacket(new ServerBoundConfirmTeleportPacket(clientBoundPlayerPositionAndLookPacket.getTeleportId()));
    }
}
