package me.dustin.chatbot.network.packet.handler;

import me.dustin.chatbot.ChatBot;
import me.dustin.chatbot.helper.GeneralHelper;
import me.dustin.chatbot.helper.MessageParser;
import me.dustin.chatbot.network.ClientConnection;
import me.dustin.chatbot.network.packet.c2s.play.ServerBoundChatPacket;
import me.dustin.chatbot.network.packet.c2s.play.ServerBoundClientStatusPacket;
import me.dustin.chatbot.network.packet.c2s.play.ServerBoundKeepAlivePacket;
import me.dustin.chatbot.network.packet.s2c.play.*;
import me.dustin.chatbot.network.player.OtherPlayer;
import me.dustin.chatbot.network.player.PlayerManager;

import java.util.UUID;

public class ClientBoundPlayClientBoundPacketHandler extends ClientBoundPacketHandler {

    public ClientBoundPlayClientBoundPacketHandler(ClientConnection clientConnection) {
        super(clientConnection);
        getPacketMap().put(0x0F, ClientBoundChatMessagePacket.class);
        getPacketMap().put(0x1A, ClientBoundDisconnectPlayPacket.class);
        getPacketMap().put(0x21, ClientBoundKeepAlivePacket.class);
        getPacketMap().put(0x35, ClientBoundPlayerDeadPacket.class);
        getPacketMap().put(0x36, ClientBoundPlayerInfoPacket.class);
        getPacketMap().put(0x52, ClientBoundUpdateHealthPacket.class);
        getPacketMap().put(0x59, ClientBoundWorldTimePacket.class);
    }

    public void handleDisconnectPacket(ClientBoundDisconnectPlayPacket clientBoundDisconnectPacket) {
        GeneralHelper.print("Disconnected: " + clientBoundDisconnectPacket.getReason(), GeneralHelper.ANSI_RED);
        getClientConnection().close();
    }

    public void handleKeepAlive(ClientBoundKeepAlivePacket keepAlivePacket) {
        //send KeepAlive packet back with same ID
        long id = keepAlivePacket.getId();
        getClientConnection().sendPacket(new ServerBoundKeepAlivePacket(id));
        getClientConnection().updateKeepAlive();
    }

    public void handleChatMessage(ClientBoundChatMessagePacket clientBoundChatMessagePacket) {
        String message = MessageParser.INSTANCE.parse(clientBoundChatMessagePacket.getMessage());
        UUID sender = clientBoundChatMessagePacket.getSender();
        GeneralHelper.print(message, GeneralHelper.ANSI_CYAN);
        if (!getClientConnection().getCommandManager().parse(MessageParser.INSTANCE.parse(clientBoundChatMessagePacket.getMessage()), sender) && ChatBot.getConfig().isCrackedLogin()) {
            if (message.contains("/register")) {
                getClientConnection().sendPacket(new ServerBoundChatPacket("/register " + ChatBot.getConfig().getCrackedLoginPassword() + " " + ChatBot.getConfig().getCrackedLoginPassword()));
            } else if (message.contains("/login")) {
                getClientConnection().sendPacket(new ServerBoundChatPacket("/login " + ChatBot.getConfig().getCrackedLoginPassword()));
            }
        }
    }

    public void handlePlayerInfoPacket(ClientBoundPlayerInfoPacket clientBoundPlayerInfoPacket) {
        for (OtherPlayer player : clientBoundPlayerInfoPacket.getPlayers()) {
            switch (clientBoundPlayerInfoPacket.getAction()) {
                case ClientBoundPlayerInfoPacket.ADD_PLAYER -> {
                    if (!PlayerManager.INSTANCE.getPlayers().contains(player)) {
                        PlayerManager.INSTANCE.getPlayers().add(player);
                    }
                }
                case ClientBoundPlayerInfoPacket.REMOVE_PLAYER -> {
                    if (player != null && PlayerManager.INSTANCE.getPlayers().contains(player)) {
                        PlayerManager.INSTANCE.getPlayers().remove(player);
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
        getClientConnection().sendPacket(new ServerBoundClientStatusPacket(ServerBoundClientStatusPacket.RESPAWN));
    }
}
