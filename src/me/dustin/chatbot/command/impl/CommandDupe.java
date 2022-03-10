package me.dustin.chatbot.command.impl;

import me.dustin.chatbot.command.Command;

public class CommandDupe extends Command {
    public CommandDupe() {
        super("dupe");
    }

    @Override
    public void run(String str) {
        if (str.isEmpty()) {
            sendChat("You have to tell me what item you want duped!");
            return;
        }
        sendChat("Successfully duped " + str + "!");
    }
}
