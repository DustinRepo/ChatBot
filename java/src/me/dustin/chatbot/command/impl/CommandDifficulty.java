package me.dustin.chatbot.command.impl;

import me.dustin.chatbot.command.Command;

import java.util.UUID;

public class CommandDifficulty extends Command {
    public CommandDifficulty() {
        super("difficulty");
    }

    @Override
    public void run(String str, UUID sender) {
        sendChat("The server is on " + getClientConnection().getWorld().getDifficulty().getName() + " difficulty");
    }
}
