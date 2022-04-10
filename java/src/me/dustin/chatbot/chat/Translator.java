package me.dustin.chatbot.chat;

import com.google.gson.JsonObject;
import me.dustin.chatbot.helper.GeneralHelper;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class Translator {

    private static JsonObject translations;

    public static void setTranslation(String translation) {
        try {
            ClassLoader loader = Thread.currentThread().getContextClassLoader();
            InputStream is = loader.getResourceAsStream("translations/" + translation + ".json");
            InputStreamReader streamReader = new InputStreamReader(is, StandardCharsets.UTF_8);
            BufferedReader reader = new BufferedReader(streamReader);
            StringBuilder sb = new StringBuilder();
            for (String line; (line = reader.readLine()) != null;) {
                sb.append(line);
            }
            is.close();
            reader.close();
            translations = GeneralHelper.gson.fromJson(sb.toString(), JsonObject.class);
        } catch (Exception e) {
            e.printStackTrace();
            GeneralHelper.print("Error grabbing translation file: " + translation + ".json", ChatMessage.TextColor.DARK_RED);
        }
    }

    public static String translate(String s) {
        if (translations.get(s) == null)
            return s;
        return translations.get(s).getAsString();
    }

}
