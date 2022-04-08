package me.dustin.chatbot.command.impl;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import me.dustin.chatbot.command.Command;
import me.dustin.chatbot.helper.GeneralHelper;
import me.dustin.chatbot.helper.MCAPIHelper;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.UUID;

public class CommandSkin extends Command {
    public CommandSkin() {
        super("skin");
    }

    @Override
    public void run(String str, UUID sender) {
        if (str.isEmpty()) {
            sendChat("Error! you have to say a player name!");
            return;
        }
        String name = str.split(" ")[0];
        new Thread(() -> {
            UUID uuid = MCAPIHelper.getUUIDFromName(name);
            if (uuid == null) {
                sendChat("Error! UUID returned null. Player may not exist.");
                return;
            }
            //going to explain what happens so I don't forget
            //request their minecraft profile, all so we can get a base64 encoded string that contains ANOTHER json that then has the skin URL
            String PROFILE_REQUEST_URL = "https://sessionserver.mojang.com/session/minecraft/profile/%s";
            String profileResponse = GeneralHelper.httpRequest(String.format(PROFILE_REQUEST_URL, uuid.toString().replace("-", "")), null, null, "GET").data();

            JsonObject object = GeneralHelper.prettyGson.fromJson(profileResponse, JsonObject.class);
            //Get the properties array which has what we need
            JsonArray array = object.getAsJsonArray("properties");
            JsonObject property = array.get(0).getAsJsonObject();
            //value is what we grab but it's encoded so we have to decode it
            String base64String = property.get("value").getAsString();
            byte[] bs = Base64.getDecoder().decode(base64String);
            //Convert the response to json and pull the skin url from there
            String secondResponse = new String(bs, StandardCharsets.UTF_8);
            JsonObject finalResponseObject = GeneralHelper.prettyGson.fromJson(secondResponse, JsonObject.class);
            JsonObject texturesObject = finalResponseObject.getAsJsonObject("textures");
            JsonObject skinObj = texturesObject.getAsJsonObject("SKIN");
            String skinURL = skinObj.get("url").getAsString();

            sendChat(skinURL);
        }).start();
    }
}
