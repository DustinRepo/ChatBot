package me.dustin.chatbot.command.impl;

import me.dustin.chatbot.command.Command;
import me.dustin.chatbot.helper.GeneralHelper;

public class CommandBible extends Command {
    public CommandBible() {
        super("bible");
        getAlias().add("bibleverse");
        getAlias().add("pray");
    }

    @Override
    public void run(String str) {
        String verse = GeneralHelper.httpRequest("https://labs.bible.org/api/?passage=random", null, null, "GET").data().replace("<b>", "").replace("</b>", "").replace("\n", " ");
        sendChat(verse);
    }
}
