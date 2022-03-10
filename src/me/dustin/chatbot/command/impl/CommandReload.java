package me.dustin.chatbot.command.impl;

import me.dustin.chatbot.ChatBot;
import me.dustin.chatbot.command.Command;

import java.util.UUID;

public class CommandReload extends Command {

    public CommandReload() {
        super("reload");
    }

    @Override
    public void run(String str, UUID sender) {
        getClientConnection().getCommandManager().init();
        try {
            ChatBot.getConfig().loadConfig();
            sendChat("Reloaded commands and config!");
        } catch (Exception e) {
            sendChat("Error in config! " + e.getMessage());
        }
    }
}
