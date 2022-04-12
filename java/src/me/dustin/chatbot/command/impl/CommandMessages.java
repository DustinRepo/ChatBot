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
        String name = "you";
        if (!str.isEmpty()) {
            name = str.split(" ")[0];
            sender = MCAPIHelper.getUUIDFromName(name);
        }
        if (sender == null) {
            sendChat("Error! Could not get UUID");
            return;
        }

        QuoteProcess quoteProcess = getClientConnection().getProcessManager().get(QuoteProcess.class);
        if (quoteProcess == null) {
            sendChat("Sorry! I'm not tracking messages currently.");
            return;
        }
        ArrayList<String> quotes = QuoteProcess.quotes.get(sender.toString().replace("-", ""));
        int count = quotes == null ? 0 : quotes.size();
        sendChat("I have " + count + " messages from " + name);
    }
}
