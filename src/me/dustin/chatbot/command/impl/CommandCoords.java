package me.dustin.chatbot.command.impl;

import me.dustin.chatbot.command.Command;
import me.dustin.chatbot.network.player.ClientPlayer;
import me.dustin.chatbot.network.player.OtherPlayer;

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
            sendChat(String.format("My coords are X: %.1f Y: %.1f Z: %.1f", player.getX(), player.getY(), player.getZ()));
            return;
        }
        String name = str.split(" ")[0];
        OtherPlayer otherPlayer = getClientConnection().getPlayerManager().get(name);
        if (otherPlayer == null) {
            sendChat("Error! " + name + " is not online!");
            return;
        }
        Random r = new Random();
        sendChat(otherPlayer.getName() + "'s coords are X:" + r.nextInt(1000000) + " Y:" + (r.nextInt(320) - 64) + " Z:" + r.nextInt(1000000));
    }
}
