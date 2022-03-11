package me.dustin.chatbot.command.impl;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import me.dustin.chatbot.command.Command;
import me.dustin.chatbot.helper.GeneralHelper;

import java.util.UUID;

public class CommandDox extends Command {

    public CommandDox() {
        super("dox");
    }

    @Override
    public void run(String str, UUID sender) {
        if (str.isEmpty()) {
            sendChat("Error! You have to tell me who to dox!");
            return;
        }
        if (str.equalsIgnoreCase(getClientConnection().getSession().getUsername())) {
            sendChat("Fuck you idiot");
            return;
        }
        String json = GeneralHelper.httpRequest("https://randomuser.me/api/?format=json", null, null, "GET").data();
        System.out.println(json);
        JsonObject firstObj = GeneralHelper.gson.fromJson(json, JsonObject.class);
        JsonArray array = firstObj.getAsJsonArray("results");
        JsonObject object = array.get(0).getAsJsonObject();

        String address;
        String fullName;
        String phonenumber;
        JsonObject nameObject = object.getAsJsonObject("name");
        JsonObject addressObject = object.getAsJsonObject("location");
        JsonObject streetObject = addressObject.get("street").getAsJsonObject();
        fullName = nameObject.get("first").getAsString() + " " + nameObject.get("last").getAsString();
        address = streetObject.get("number").getAsString() + " " + streetObject.get("name").getAsString() + ", " + addressObject.get("city").getAsString() + " " + addressObject.get("state").getAsString();
        phonenumber = object.get("cell").getAsString();

        sendChat(str + "'s Name: " + fullName + ". Address: " + address + ". Phone#: " + phonenumber);
    }
}
