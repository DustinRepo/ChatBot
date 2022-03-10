package me.dustin.chatbot.command.impl;

import me.dustin.chatbot.command.Command;

public class CommandKys extends Command {
    public CommandKys() {
        super("kys");
    }

    @Override
    public void run(String str) {
        sendChat("/suicide");
    }
}
