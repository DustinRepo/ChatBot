package me.dustin.chatbot.command.impl;

import com.google.gson.JsonObject;
import me.dustin.chatbot.command.Command;
import me.dustin.chatbot.helper.GeneralHelper;

import java.util.UUID;

public class CommandKanye extends Command {
    public CommandKanye() {
        super("kanye");
        getAlias().add("ye");
    }

    @Override
    public void run(String str, UUID sender) {
        String resp = GeneralHelper.httpRequest("https://api.kanye.rest", null, null, "GET").data();
        JsonObject jsonObject = GeneralHelper.gson.fromJson(resp, JsonObject.class);
        sendChat("Kanye says: " + jsonObject.get("quote").getAsString());
    }
}
