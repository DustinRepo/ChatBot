package me.dustin.chatbot.command.impl;

import me.dustin.chatbot.command.Command;
import me.dustin.chatbot.helper.GeneralHelper;
import me.dustin.chatbot.helper.MCAPIHelper;
import me.dustin.chatbot.process.impl.QuoteProcess;

import java.util.ArrayList;
import java.util.UUID;

public class CommandWordCount extends Command {
    public CommandWordCount() {
        super("wordcount");
        getAlias().add("count");
    }

    @Override
    public void run(String str, UUID sender) {
        if (str.isEmpty() || str.split(" ").length < 2) {
            sendChat("Error! You have to specify a player name and a word / phrase!");
            return;
        }
        String name = str.split(" ")[0];
        String phrase = str.replace(name + " ", "");
        if (name.equalsIgnoreCase(getClientConnection().getSession().getUsername())) {
            sendChat("Sorry, I don't track myself in this.");
            return;
        }
        QuoteProcess quoteProcess = getClientConnection().getProcessManager().get(QuoteProcess.class);
        if (quoteProcess == null) {
            sendChat("Sorry! I'm not tracking messages currently.");
            return;
        }

        UUID uuid = MCAPIHelper.getUUIDFromName(name);
        if (uuid == null) {
            sendChat("Error! UUID returned null on name: " + name);
            return;
        }
        int count = 0;
        ArrayList<String> quotes = quoteProcess.quotes.get(uuid.toString().replace("-", ""));
        for (String quote : quotes) {
            count += GeneralHelper.countMatches(quote, phrase);
        }
        if (count == 0) {
            sendChat(name + " hasn't said \"" + phrase + "\" yet.");
            return;
        }
        sendChat(name + " has said \"" + phrase +"\" " + count + " times.");
    }
}
