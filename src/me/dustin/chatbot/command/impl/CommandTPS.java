package me.dustin.chatbot.command.impl;

import me.dustin.chatbot.ChatBot;
import me.dustin.chatbot.command.Command;

public class CommandTPS extends Command {
    public CommandTPS() {
        super("tps");
    }

    @Override
    public void run(String str) {
        sendChat(String.format("Current tps: %.1f", ChatBot.getClientConnection().getTpsHelper().getAverageTPS()));
    }
}
