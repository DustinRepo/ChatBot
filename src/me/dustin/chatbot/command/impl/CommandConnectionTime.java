package me.dustin.chatbot.command.impl;

import me.dustin.chatbot.ChatBot;
import me.dustin.chatbot.command.Command;
import me.dustin.chatbot.helper.GeneralHelper;

import java.util.UUID;

public class CommandConnectionTime extends Command {
    public CommandConnectionTime() {
        super("connected");
    }

    @Override
    public void run(String str, UUID sender) {
        String time = GeneralHelper.getDurationString(ChatBot.connectionTime());
        sendChat("I have been connected for " + time);
    }
}
