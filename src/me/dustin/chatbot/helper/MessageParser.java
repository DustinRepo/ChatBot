package me.dustin.chatbot.helper;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import me.dustin.chatbot.ChatBot;

public enum MessageParser {
    INSTANCE;

    public String parse(String jsonData) {
        StringBuilder s = new StringBuilder();
        JsonObject jsonObject = GeneralHelper.gson.fromJson(jsonData, JsonObject.class);
        JsonArray with = jsonObject.getAsJsonArray("with");
        if (with != null) {
            for (int i = 0; i < with.size(); i++) {
                try {
                    JsonObject object = with.get(i).getAsJsonObject();
                    String text = object.get("text").getAsString();
                    s.append("<").append(text).append("> ");
                } catch (Exception e) {
                }
            }
            s.append(with.get(1).getAsString());
        }
        JsonArray extra = jsonObject.getAsJsonArray("extra");
        if (extra != null) {
            for (int i = 0; i < extra.size(); i++) {
                try {
                    JsonObject object = extra.get(i).getAsJsonObject();
                    String text = object.get("text").getAsString();
                    s.append(text);
                } catch (Exception e) {
                }
            }
        }
        return s.toString();
    }
}
