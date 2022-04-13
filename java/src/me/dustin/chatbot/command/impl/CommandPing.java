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
                sendChat("ChatBot error! Could not get your UUID!", sender);
                return;
            }
            OtherPlayer player = getClientConnection().getPlayerManager().get(sender);
            if (player == null) {
                sendChat("Error! Couldn't find you in my player list :(", sender);
                return;
            }
            if (player.getPing() <= 0)
                sendChat("Server doesn't have a ping count for you yet", sender);
            else
                sendChat("Your ping is " + player.getPing() + "ms", sender);
        } else {
            String name = str.split(" ")[0];
            OtherPlayer player = getClientConnection().getPlayerManager().get(name);
            if (player == null) {
                sendChat("Error! " + name + " not online!", sender);
                return;
            }
            if (player.getPing() <= 0)
                sendChat("Server doesn't have a ping count for " + player.getName() + " yet", sender);
            else
                sendChat(player.getName() + "'s ping is " + player.getPing() + "ms", sender);
        }
    }
}
