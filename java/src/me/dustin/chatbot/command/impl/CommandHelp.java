package me.dustin.chatbot.command.impl;

import me.dustin.chatbot.command.Command;

import java.util.UUID;

public class CommandHelp extends Command {
    public CommandHelp() {
        super("help");
    }

    @Override
    public void run(String str, UUID sender) {
        sendChat("ChatBot made by Dustin. List of commands: https://github.com/DustinRepo/ChatBot#commands", sender);
    }
}
