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
        if (str.equalsIgnoreCase(getClientConnection().getSession().getUsername())) {
            sendChat("Fuck you buddy");
            return;
        }
        sendChat("Started a vote to kill " + str + ". Vote with /kill " + str);
    }
}
