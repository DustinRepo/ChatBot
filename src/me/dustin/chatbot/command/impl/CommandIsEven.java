package me.dustin.chatbot.command.impl;

import com.google.gson.JsonObject;
import me.dustin.chatbot.command.Command;
import me.dustin.chatbot.helper.GeneralHelper;

import java.util.UUID;

public class CommandIsEven extends Command {
    public CommandIsEven() {
        super("iseven");
    }

    @Override
    public void run(String str, UUID sender) {
        try {
            int num = Integer.parseInt(str);
            String api = "https://api.isevenapi.xyz/api/iseven/%d/";

            String resp = GeneralHelper.httpRequest(String.format(api, num), null, null, "GET").data();
            JsonObject jsonObject = GeneralHelper.gson.fromJson(resp, JsonObject.class);
            if (jsonObject != null) {
                String ad = jsonObject.get("ad").getAsString();
                boolean isEven = jsonObject.get("iseven").getAsBoolean();
                sendChat("IsEven: " + isEven + " | AD: " + ad);
            }
        } catch (NumberFormatException e) {
            sendChat("Error! Not a number!");
        }
    }
}
