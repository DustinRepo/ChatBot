package me.dustin.chatbot.command.impl;

import me.dustin.chatbot.command.Command;

import java.util.UUID;

public class CommandReload extends Command {

    public CommandReload() {
        super("reload");
    }

    @Override
    public void run(String str, UUID sender) {
        getClientConnection().getCommandManager().init();
        sendChat("Reloaded commands!");
    }
}
