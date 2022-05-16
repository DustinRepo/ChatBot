package me.dustin.chatbot.command.impl;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import me.dustin.chatbot.command.Command;
import me.dustin.chatbot.helper.GeneralHelper;

import java.util.ArrayList;
import java.util.Random;
import java.util.UUID;

public class CommandBender extends Command {
    public CommandBender() {
        super("bender");
    }
    private ArrayList<String> quotes = new ArrayList<>();
    @Override
    public void run(String str, UUID sender) {
        if (quotes.isEmpty()) {
            String data = GeneralHelper.httpRequest("http://futuramaapi.herokuapp.com/api/characters/bender", null, null, "GET").data();
            JsonArray jsonArray = GeneralHelper.gson.fromJson(data, JsonArray.class);
            jsonArray.forEach(jsonElement -> {
                JsonObject object = (JsonObject) jsonElement;
                quotes.add(object.get("quote").getAsString());
            });
            Random r = new Random();
            String quote = quotes.get(r.nextInt(quotes.size()));
            sendChat("Bender says: %s".formatted(quote), sender);
        }

    }
}
