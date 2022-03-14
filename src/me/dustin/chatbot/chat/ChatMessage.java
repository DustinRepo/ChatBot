package me.dustin.chatbot.chat;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import me.dustin.chatbot.helper.GeneralHelper;

public class ChatMessage {

    public String senderName;
    public String body;

    public ChatMessage(String senderName, String body) {
        this.senderName = senderName;
        this.body = body;
    }

    public String getMessage() {
        if (senderName.isEmpty())
            return body;
        return senderName + " " + body;
    }

    public String getSenderName() {
        return senderName;
    }

    public String getBody() {
        return body;
    }

    public static ChatMessage of(String jsonData) {
        StringBuilder name = new StringBuilder();
        StringBuilder body = new StringBuilder();
        JsonObject jsonObject = GeneralHelper.gson.fromJson(jsonData, JsonObject.class);
        JsonArray with = jsonObject.getAsJsonArray("with");
        if (with != null) {
            for (int i = 0; i < with.size(); i++) {
                try {
                    JsonObject object = with.get(i).getAsJsonObject();
                    if (object.get("color") != null) {
                        name.append("§").append(colorChar(object.get("color").getAsString()));
                    }
                    String text = object.get("text").getAsString();
                    name.append(text);
                } catch (Exception e) {
                    try {
                        body.append(" ").append(with.get(i).getAsString());
                    } catch (Exception e1) {}
                }
            }
        }
        if (jsonObject.get("translate") != null) {
            String translate = jsonObject.get("translate").getAsString();
            if (!translate.equalsIgnoreCase("chat.type.text"))
                body.append(" ").append(translate);
            else
                name = new StringBuilder("<").append(name).append(">");
        }
        body.append(getExtra(jsonObject));
        if (body.toString().startsWith("<") && body.toString().contains("> ") && name.toString().isEmpty()) {//crude way to move player name to actual name field if the text is set up weird
            String s = body.toString().split("<")[1].split(">")[0];
            name.append(s);
            body = new StringBuilder(body.toString().replace("<" + s + "> ", ""));
        }
        if (body.toString().startsWith(" ")) {
            body = new StringBuilder(body.substring(1));
        }
        if (jsonObject.get("text") != null) {
            body.append(jsonObject.get("text").getAsString());
        }
        return new ChatMessage(name.toString(), body.toString());
    }

    private static String getExtra(JsonObject jsonObject) {
        StringBuilder s = new StringBuilder();
        JsonArray extra = jsonObject.getAsJsonArray("extra");
        if (extra != null) {
            for (int i = 0; i < extra.size(); i++) {
                try {
                    JsonObject object = extra.get(i).getAsJsonObject();
                    if (object.get("color") != null) {
                        s.append("§").append(colorChar(object.get("color").getAsString()));
                    }
                    String text = object.get("text").getAsString();
                    s.append(text);

                    if (object.get("extra") != null)
                        s.append(getExtra(object));
                } catch (Exception e) {
                    s.append(" ").append(extra.get(i).getAsString());
                }
            }
        }
        return s.toString();
    }

    private static char colorChar(String name) {
        switch (name.toLowerCase()) {
            case "dark_red" -> { return '4'; }
            case "red" -> { return 'c'; }
            case "gold" -> { return '6'; }
            case "yellow" -> { return 'e'; }
            case "dark_green)" -> { return '2'; }
            case "green" -> { return 'a'; }
            case "aqua" -> { return 'b'; }
            case "dark_aqua" -> { return '3'; }
            case "dark_blue" -> { return '1'; }
            case "blue" -> { return '9'; }
            case "light_purple" -> { return 'd'; }
            case "dark_purple" -> { return '5'; }
            case "white" -> { return 'f'; }
            case "gray" -> { return '7'; }
            case "dark_gray" -> { return '8'; }
            case "black" -> { return '0'; }
        }
        return 'f';
    }
}
