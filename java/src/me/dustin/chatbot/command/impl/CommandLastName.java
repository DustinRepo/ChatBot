package me.dustin.chatbot.command.impl;

import me.dustin.chatbot.command.Command;
import me.dustin.chatbot.helper.MCAPIHelper;

import java.util.Map;
import java.util.UUID;

public class CommandLastName extends Command {
    public CommandLastName() {
        super("lastname");
    }

    @Override
    public void run(String str, UUID sender) {
        UUID uuid = MCAPIHelper.getUUIDFromName(str.split(" ")[0]);
        if (uuid == null) {
            sendChat("Error! Could not grab UUID from username " + str, sender);
            return;
        }
        String name = MCAPIHelper.getLastChangedName(uuid);
        if (name != null) {
            sendChat(str + "'s most recent name was " + name, sender);
        } else {
            sendChat(str + " has never changed their name.", sender);
        }
    }
}
