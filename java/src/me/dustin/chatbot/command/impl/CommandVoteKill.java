package me.dustin.chatbot.command.impl;

import me.dustin.chatbot.command.Command;

import java.util.UUID;

public class CommandVoteKill extends Command {
    public CommandVoteKill() {
        super("votekill");
    }

    @Override
    public void run(String str, UUID sender) {
        if (str.isEmpty()) {
            sendChat("You have to tell me who you want killed!");
            return;
        }
        String name = str.split(" ")[0];
        if (name.equalsIgnoreCase(getClientConnection().getSession().getUsername())) {
            sendChat("Fuck you buddy");
            return;
        }
        sendChat("Started a vote to kill " + name + ". Vote with /kill " + str);
    }
}
