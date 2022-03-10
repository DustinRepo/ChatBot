package me.dustin.chatbot.command.impl;

import me.dustin.chatbot.command.Command;

public class CommandHelp extends Command {
    public CommandHelp() {
        super("help");
    }

    @Override
    public void run(String str) {
        sendChat("ChatBot made by Dustin. List of commands: https://github.com/DustinRepo/ChatBot");
    }
}
