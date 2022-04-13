package me.dustin.chatbot.command.impl;

import me.dustin.chatbot.command.Command;
import me.dustin.chatbot.helper.MCAPIHelper;
import me.dustin.chatbot.process.impl.QuoteProcess;

import java.util.ArrayList;
import java.util.UUID;

public class CommandMessages extends Command {
    public CommandMessages() {
        super("messages");
    }

    @Override
    public void run(String str, UUID sender) {
        UUID uuid = sender;
        String name = "you";
        if (!str.isEmpty()) {
            name = str.split(" ")[0];
            uuid = MCAPIHelper.getUUIDFromName(name);
        }
        if (uuid == null) {
            sendChat("Error! Could not get UUID", sender);
            return;
        }

        QuoteProcess quoteProcess = getClientConnection().getProcessManager().get(QuoteProcess.class);
        if (quoteProcess == null) {
            sendChat("Sorry! I'm not tracking messages currently.", sender);
            return;
        }
        ArrayList<String> quotes = QuoteProcess.quotes.get(uuid.toString().replace("-", ""));
        int count = quotes == null ? 0 : quotes.size();
        sendChat("I have " + count + " messages from " + name, sender);
    }
}
