package me.dustin.chatbot.command.impl;

import me.dustin.chatbot.command.Command;
import me.dustin.chatbot.entity.player.PlayerInfo;

import java.util.UUID;

public class CommandNameMC extends Command {

    public CommandNameMC() {
        super("namemc");
    }

    @Override
    public void run(String str, UUID sender) {
        if (str.isEmpty()) {
            PlayerInfo player = getClientConnection().getPlayerManager().get(sender);
            if (player == null) {
                sendChat("Error! Couldn't find you in my player list :(", sender);
                return;
            }
            sendChat("https://namemc.com/search?q=" + player.getName(), sender);
            return;
        }
        String name = str.split(" ")[0];
        sendChat("https://namemc.com/search?q=" + name, sender);
    }
}
