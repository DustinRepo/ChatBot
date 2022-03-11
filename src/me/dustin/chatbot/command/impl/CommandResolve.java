package me.dustin.chatbot.command.impl;

import me.dustin.chatbot.command.Command;

import java.util.Random;
import java.util.UUID;

public class CommandResolve extends Command {
    public CommandResolve() {
        super("resolve");
        getAlias().add("ip");
    }

    @Override
    public void run(String str, UUID sender) {
        if (str.isEmpty()) {
            sendChat("Error! You have to tell me who to dox!");
            return;
        }
        if (str.equalsIgnoreCase(getClientConnection().getSession().getUsername())) {
            sendChat("Nice try, dumbass");
            return;
        }
        Random r = new Random();
        String ip = r.nextInt(256) + "." + r.nextInt(256) + "." + r.nextInt(256) + "." + r.nextInt(256);
        sendChat(str + "'s IP is " + ip);
    }
}
