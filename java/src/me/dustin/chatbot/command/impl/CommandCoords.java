package me.dustin.chatbot.command.impl;

import me.dustin.chatbot.command.Command;
import me.dustin.chatbot.entity.player.PlayerInfo;
import me.dustin.chatbot.entity.player.ClientPlayer;

import java.util.Random;
import java.util.UUID;

public class CommandCoords extends Command {
    public CommandCoords() {
        super("coords");
    }

    @Override
    public void run(String str, UUID sender) {
        if (str.isEmpty() || str.split(" ")[0].equalsIgnoreCase(getClientConnection().getSession().getUsername())) {
            ClientPlayer player = getClientConnection().getClientPlayer();
            sendChat(String.format("My coords are X: %.1f Y: %.1f Z: %.1f", player.getX(), player.getY(), player.getZ()), sender);
            return;
        }
        String name = str.split(" ")[0];
        PlayerInfo playerInfo = getClientConnection().getPlayerManager().get(name);
        if (playerInfo == null) {
            sendChat("Error! " + name + " is not online!", sender);
            return;
        }
        Random r = new Random();
        sendChat(playerInfo.getName() + "'s coords are X:" + (r.nextInt(2000000) - 1000000) + " Z:" + (r.nextInt(2000000) - 1000000), sender);
    }
}
