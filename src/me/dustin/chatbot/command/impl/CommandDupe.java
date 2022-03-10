package me.dustin.chatbot.command.impl;

import me.dustin.chatbot.command.Command;
import me.dustin.chatbot.network.player.OtherPlayer;
import me.dustin.chatbot.network.player.PlayerManager;

import java.util.UUID;

public class CommandDupe extends Command {
    public CommandDupe() {
        super("dupe");
    }

    @Override
    public void run(String str, UUID sender) {
        if (str.isEmpty()) {
            sendChat("You have to tell me what item you want duped!");
            return;
        }
        OtherPlayer player = PlayerManager.INSTANCE.get(sender);
        sendChat((player == null ? "Successfully " : player.getName() + " just ") + "duped " + str + "!");
    }
}
