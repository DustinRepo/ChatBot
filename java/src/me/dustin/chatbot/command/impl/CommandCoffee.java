package me.dustin.chatbot.command.impl;

import com.google.gson.JsonObject;
import me.dustin.chatbot.command.Command;
import me.dustin.chatbot.helper.GeneralHelper;

import java.util.UUID;

public class CommandCoffee extends Command {
    public CommandCoffee() {
        super("coffee");
    }

    @Override
    public void run(String str, UUID send) {
        String resp = GeneralHelper.httpRequest("https://coffee.alexflipnote.dev/random.json", null, null, "GET").data();
        JsonObject jsonObject = GeneralHelper.gson.fromJson(resp, JsonObject.class);
        sendChat(jsonObject.get("file").getAsString());
    }
}
