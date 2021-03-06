package me.dustin.chatbot.chat;

import com.google.gson.JsonObject;
import me.dustin.chatbot.helper.GeneralHelper;
import me.dustin.chatbot.network.ProtocolHandler;

public class Translator {

    private static JsonObject translations;

    public static void setTranslation(String translation) {
        try {
            String v = ProtocolHandler.getCurrent().getName().replace(".", "_");
            if (v.toLowerCase().contains("pre") || v.toLowerCase().contains("w"))
                v = "1_19";
            //fat fuckin mess to create the version id needed for the link, i.e. 1_12 from 1.12.2
            if (v.split("_").length > 2)
                v = v.split("_")[0] + "_" + v.split("_")[1];
            boolean useJson = ProtocolHandler.getCurrent().getProtocolVer() >= ProtocolHandler.getVersionFromName("1.13").getProtocolVer();
            //make the last two digits uppercase if 1.8 or below
            if (ProtocolHandler.getCurrent().getProtocolVer() <= ProtocolHandler.getVersionFromName("1.8.9").getProtocolVer())
                translation = translation.substring(0, 3) + translation.substring(3).toUpperCase();
            else
                translation = translation.toLowerCase();

            String url = "https://raw.githubusercontent.com/DustinRepo/MC-Translations/main/" + v + "/" + translation + (useJson ? ".json" : ".lang");
            String data = GeneralHelper.httpRequest(url, null, null, "GET").data();
            if (useJson) {
                translations = GeneralHelper.gson.fromJson(data, JsonObject.class);
            } else {
                translations = new JsonObject();
                for (String s : data.split("\n")) {
                    if (s.contains("="))
                        translations.addProperty(s.split("=")[0], s.split("=")[1]);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String translate(String s) {
        if (translations == null || translations.get(s) == null)
            return s;
        return translations.get(s).getAsString();
    }

}
