package me.dustin.chatbot.command.impl;

import me.dustin.chatbot.command.Command;
import me.dustin.chatbot.network.player.OtherPlayer;

import java.util.UUID;

public class CommandDupe extends Command {
    public CommandDupe() {
        super("dupe");
    }

    @Override
    public void run(String str, UUID sender) {
        if (str.isEmpty()) {
            sendChat("You have to tell me what item you want duped!", sender);
            return;
        }
        OtherPlayer player = getClientConnection().getPlayerManager().get(sender);
        sendChat((player == null ? "Successfully " : player.getName() + " just ") + "duped " + str + "!", sender);
    }
}
