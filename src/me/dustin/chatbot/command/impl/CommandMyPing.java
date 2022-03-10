package me.dustin.chatbot.command.impl;

import me.dustin.chatbot.command.Command;
import me.dustin.chatbot.network.player.OtherPlayer;

import java.util.UUID;

public class CommandMyPing extends Command {
    public CommandMyPing() {
        super("myping");
    }

    @Override
    public void run(String str, UUID sender) {
        if (sender == null) {
            sendChat("ChatBot error! Could not get your UUID!");
            return;
        }
        OtherPlayer player = getClientConnection().getPlayerManager().get(sender);
        if (player == null) {
            sendChat("Error! Couldn't find you in my player list :(");
            return;
        }
        sendChat("Your ping is: " + player.getPing());
    }
}
