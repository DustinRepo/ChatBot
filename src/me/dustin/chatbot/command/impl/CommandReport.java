package me.dustin.chatbot.command.impl;

import me.dustin.chatbot.command.Command;

import java.util.UUID;

public class CommandReport extends Command {
    public CommandReport() {
        super("report");
    }

    @Override
    public void run(String str, UUID sender) {
        if (str.isEmpty()) {
            sendChat("Error! You must specify a player to report!");
            return;
        }
        String name = str;
        if (!name.contains(" ")) {
            if (name.equalsIgnoreCase(getClientConnection().getSession().getUsername())) {
                sendChat("Eat shit, fatass");
                return;
            }
            sendChat(name + " has been reported. Administrators will handle them shortly.");
            return;
        }
        name = str.split(" ")[0];
        String reason = str.replace(name + " ", "");

        if (name.equalsIgnoreCase(getClientConnection().getSession().getUsername())) {
            sendChat("Eat shit, fatass");
            return;
        }

        sendChat(name + " has been reported for: " + reason + ". Administrators will handle them shortly.");
    }
}
