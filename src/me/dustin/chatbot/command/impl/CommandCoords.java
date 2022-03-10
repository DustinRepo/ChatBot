package me.dustin.chatbot.command.impl;

import me.dustin.chatbot.command.Command;
import me.dustin.chatbot.network.player.ClientPlayer;

import java.util.UUID;

public class CommandCoords extends Command {
    public CommandCoords() {
        super("coords");
    }

    @Override
    public void run(String str, UUID sender) {
        ClientPlayer player = getClientConnection().getClientPlayer();
        sendChat(String.format("My coords are X: %.1f Y: %.1f Z: %.1f", player.getX(), player.getY(), player.getZ()));
    }
}
