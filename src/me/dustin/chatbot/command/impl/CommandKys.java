package me.dustin.chatbot.command.impl;

import me.dustin.chatbot.command.Command;

import java.util.UUID;

public class CommandKys extends Command {
    public CommandKys() {
        super("kys");
    }

    @Override
    public void run(String str, UUID sender) {
        sendChat("/suicide");
    }
}
