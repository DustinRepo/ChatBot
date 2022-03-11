package me.dustin.chatbot.command.impl;

import me.dustin.chatbot.command.Command;
import me.dustin.chatbot.network.player.OtherPlayer;

import java.util.UUID;

public class CommandPing extends Command {
    public CommandPing() {
        super("ping");
    }

    @Override
    public void run(String str, UUID sender) {
        if (str.isEmpty()) {
            if (sender == null) {
                sendChat("ChatBot error! Could not get your UUID!");
                return;
            }
            OtherPlayer player = getClientConnection().getPlayerManager().get(sender);
            if (player == null) {
                sendChat("Error! Couldn't find you in my player list :(");
                return;
            }
            if (player.getPing() <= 0)
                sendChat("Server doesn't have a ping count for you yet");
            else
                sendChat("Your ping is " + player.getPing() + "ms");
        } else {
            OtherPlayer player = getClientConnection().getPlayerManager().get(str);
            if (player == null) {
                sendChat("Error! " + str + " not online!");
                return;
            }
            if (player.getPing() <= 0)
                sendChat("Server doesn't have a ping count for " + player.getName() + " yet");
            else
                sendChat(player.getName() + "'s ping is " + player.getPing() + "ms");
        }
    }
}
