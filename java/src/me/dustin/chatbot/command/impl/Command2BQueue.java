package me.dustin.chatbot.command.impl;

import com.google.gson.JsonObject;
import me.dustin.chatbot.command.Command;
import me.dustin.chatbot.helper.GeneralHelper;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.UUID;

public class Command2BQueue extends Command {
    public Command2BQueue() {
        super("queue");
        getAlias().add("q");
    }

    @Override
    public void run(String str, UUID sender) {
        String resp = GeneralHelper.httpRequest("https://2bqueue.info/*", null, null, "GET").data();
        JsonObject object = GeneralHelper.gson.fromJson(resp, JsonObject.class);
        int regQueue = object.get("regular").getAsInt();
        int priority = object.get("prio").getAsInt();

        sendChat("2B2T queue is currently: " + regQueue + " players long. Priority Queue: " + priority, sender);
    }
}
