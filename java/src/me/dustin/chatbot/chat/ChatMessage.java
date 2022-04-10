package me.dustin.chatbot.chat;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import me.dustin.chatbot.ChatBot;
import me.dustin.chatbot.helper.GeneralHelper;

import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.util.StringJoiner;

public class ChatMessage {

    private String senderName;
    private String body;

    private static final ChatMessage parsingMessage = new ChatMessage("", "");

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

    public static String parse(JsonElement element) {
        StringJoiner sj = new StringJoiner(" ");
        if (element.isJsonPrimitive())
            return element.getAsString();
        if (!element.isJsonObject()) {
            JsonArray array = element.getAsJsonArray();
            for (JsonElement jsonElement : array) {
                sj.add(parse(jsonElement));
            }
            return sj.toString();
        } else {
            JsonObject jsonObject = element.getAsJsonObject();
            StringBuilder sb = new StringBuilder();
            if (jsonObject.get("color") != null) {
                TextColor textColor = TextColor.getFromName(jsonObject.get("color").getAsString());
                if (textColor != null)
                    sb.append("§").append(textColor.getChar());
            }
            if (jsonObject.has("text")) {
                if (jsonObject.has("translate") && jsonObject.get("translate").getAsString().equalsIgnoreCase("chat.type.text")) {
                    parsingMessage.senderName = sb.append(jsonObject.get("text").getAsString()).toString();
                } else
                    sj.add(sb.append(jsonObject.get("text").getAsString()).toString());
            } else {
                String translate = "";
                if (jsonObject.has("translate")) {
                    translate = jsonObject.get("translate").getAsString();
                    if (jsonObject.has("with")) {
                        JsonArray array = jsonObject.getAsJsonArray("with");
                        String[] args = new String[array.size()];
                        for (int i = 0; i < array.size(); i++) {
                            args[i] = parse(array.get(i));
                        }

                        String translated = Translator.translate(translate);
                        for (int i = 0; i < args.length; i++) {
                            String arg = args[i];
                            translated = translated.replaceFirst("%(?:(\\d+)\\$)?([A-Za-z%]|$)", arg);
                        }
                        sj.add(sb.append(translated));
                    } else {
                        sj.add(sb.append(Translator.translate(translate)));
                    }
                }
            }
            if (jsonObject.has("extra")) {
                sj.add(getExtra(jsonObject));
            }
            return sj.toString().trim();
        }
    }

    public static ChatMessage of(String jsonData) {
        parsingMessage.senderName = "";
        parsingMessage.body = "";
        JsonObject jsonObject = GeneralHelper.gson.fromJson(jsonData, JsonObject.class);
        parsingMessage.body = parse(jsonObject);
        String body = parsingMessage.body;
        if (body.startsWith("<") && body.contains("> ") && parsingMessage.senderName.toString().isEmpty()) {//crude way to move player name to actual name field if the text is set up weird
            String s = body.split("<")[1].split(">")[0];
            parsingMessage.senderName = s;
            parsingMessage.body = body.replace("<" + s + "> ", "");
        }
        return parsingMessage;
    }

    private static String getExtra(JsonObject jsonObject) {
        StringBuilder s = new StringBuilder();
        JsonArray extra = jsonObject.getAsJsonArray("extra");
        if (extra != null) {
            for (int i = 0; i < extra.size(); i++) {
                try {
                    JsonObject object = extra.get(i).getAsJsonObject();
                    if (object.get("color") != null) {
                        TextColor textColor = TextColor.getFromName(object.get("color").getAsString());
                        if (textColor != null)
                            s.append("§").append(textColor.getChar());
                    }
                    String text = object.get("text").getAsString();
                    s.append(text);

                    if (object.get("extra") != null)
                        s.append(getExtra(object));
                } catch (Exception e) {
                    try {
                        s.append(" ").append(extra.get(i).getAsString());
                    } catch (Exception e1) {
                        JsonObject o = extra.get(i).getAsJsonObject();
                        if (o.get("color") != null) {
                            TextColor textColor = TextColor.getFromName(o.get("color").getAsString());
                            if (textColor != null)
                                s.append("§").append(textColor.getChar());
                        }
                        if (o.get("translate") != null) {
                            String translate = o.get("translate").getAsString();
                            if (!translate.equalsIgnoreCase("chat.type.text"))
                                s.append(" ").append(Translator.translate(translate));
                        }
                    }
                }
            }
        }
        return s.toString();
    }

    public enum TextColor {
        DARK_RED("dark_red", '4', "\u001B[31m", new Color(170, 0, 0)),
        RED("red", 'c', "\u001B[31m", new Color(255, 85, 85)),
        GOLD("gold", '6', "\u001B[33m", new Color(255, 170, 0)),
        YELLOW("yellow", 'e', "\u001B[33m", new Color(255, 255, 85)),
        DARK_GREEN("dark_green", '2', "\u001B[32m", new Color(0, 170, 0)),
        GREEN("green", 'a', "\u001B[32m", new Color(85, 255, 85)),
        AQUA("aqua", 'b', "\u001B[36m", new Color(85, 255, 255)),
        DARK_AQUA("dark_aqua", '3', "\u001B[36m", new Color(0, 170, 170)),
        DARK_BLUE("dark_blue", '1', "\u001B[34m", new Color(0, 0, 170)),
        BLUE("blue", '9', "\u001B[34m", new Color(85, 85, 255)),
        LIGHT_PURPLE("light_purple", 'd', "\u001B[35m", new Color(255, 85, 255)),
        DARK_PURPLE("dark_purple", '5', "\u001B[35m", new Color(170, 0, 170)),
        WHITE("white", 'f', "\u001B[0m", new Color(255, 255, 255)),
        GRAY("gray", '7', "\u001B[37m", new Color(170, 170, 170)),
        DARK_GRAY("dark_gray", '8', "\u001B[37m", new Color(85, 85, 85)),
        BLACK("black", '0', "\u001B[30m", new Color(0, 0, 0)),
        RESET("reset", 'r', "\u001B[0m", new Color(255, 255, 255));


        private final String ansi, name;
        private final char char_;
        private final Color color;
        private Style style;
        TextColor(String name, char char_, String ansi, Color color) {
            this.ansi = ansi;
            this.name = name;
            this.char_ = char_;
            this.color = color;
        }

        public static TextColor getFromName(String name) {
            for (TextColor value : TextColor.values()) {
                if (value.getName().equalsIgnoreCase(name))
                    return value;
            }
            return null;
        }

        public static TextColor getFromChar(char char_) {
            for (TextColor value : TextColor.values()) {
                if (value.getChar() == char_)
                    return value;
            }
            return null;
        }

        public String getAnsi() {
            return ansi;
        }

        public Style getStyle() {
            if (style == null) {
                StyledDocument document = ChatBot.getGui().getOutput().getStyledDocument();
                Style s = document.addStyle(this.getName(), null);
                StyleConstants.setForeground(s, getColor());
                style = s;
            }
            return style;
        }

        public String getName() {
            return name;
        }

        public Color getColor() {
            return color;
        }

        public char getChar() {
            return char_;
        }
    }
}
