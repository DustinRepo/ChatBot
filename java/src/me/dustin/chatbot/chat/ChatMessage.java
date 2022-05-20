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
import java.util.ArrayList;

public class ChatMessage {

    private String senderName;
    private String body;

    private static final ChatMessage parsingMessage = new ChatMessage("", "");

    public ChatMessage(String senderName, String body) {
        this.senderName = senderName;
        this.body = body;
    }

    public static ChatMessage of(String jsonData) {
        parsingMessage.senderName = "";
        parsingMessage.body = "";
        parsingMessage.body = parse(GeneralHelper.gson.fromJson(jsonData, JsonObject.class));
        String body = parsingMessage.body;
        if (body.startsWith("<") && body.contains("> ") && parsingMessage.senderName.isEmpty()) {//crude way to move player name to actual name field if the text is set up weird
            String s = body.split("<")[1].split(">")[0];
            parsingMessage.senderName = s;
            parsingMessage.body = body.replace("<" + s + "> ", "");
        }
        return parsingMessage;
    }

    public static String parse(JsonElement element) {
        StringBuilder sj = new StringBuilder();
        if (element == null)
            return "";
        if (element.isJsonPrimitive())
            return element.getAsString();
        if (!element.isJsonObject()) {
            JsonArray array = element.getAsJsonArray();
            for (JsonElement jsonElement : array) {
                sj.append(parse(jsonElement));
            }
            return sj.toString();
        } else {
            JsonObject jsonObject = element.getAsJsonObject();
            StringBuilder sb = new StringBuilder();
            if (jsonObject.get("color") != null) {
                TextColor textColor = TextColor.getFromName(jsonObject.get("color").getAsString());
                if (textColor == null && jsonObject.get("color").getAsString().startsWith("#")){
                    textColor = TextColor.getOrAdd(jsonObject.get("color").getAsString());
                }
                if (textColor != null) {
                    sb.append("__COLOR_(%03d,%03d,%03d)".formatted(textColor.getColor().getRed(), textColor.getColor().getGreen(), textColor.getColor().getBlue()));
                }
            }
            if (jsonObject.has("text")) {
                if (jsonObject.has("translate") && jsonObject.get("translate").getAsString().equalsIgnoreCase("chat.type.text")) {
                    parsingMessage.senderName = sb.append(jsonObject.get("text").getAsString()).toString();
                } else
                    sj.append(sb.append(jsonObject.get("text").getAsString()));
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
                        sj.append(sb.append(translated));
                    } else {
                        sj.append(sb.append(Translator.translate(translate)));
                    }
                }
            }
            if (jsonObject.has("extra")) {
                sj.append(getExtra(jsonObject));
            }
            return removeFormatCodes(sj.toString().trim());
        }
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
                        if (textColor == null && object.get("color").getAsString().startsWith("#")){
                            textColor = TextColor.getOrAdd(object.get("color").getAsString());
                        }
                        if (textColor != null)
                            s.append("__COLOR_(%03d,%03d,%03d)".formatted(textColor.getColor().getRed(), textColor.getColor().getGreen(), textColor.getColor().getBlue()));
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
                            if (textColor == null && o.get("color").getAsString().startsWith("#")){
                                textColor = TextColor.getOrAdd(o.get("color").getAsString());
                            }
                            if (textColor != null)
                                s.append("__COLOR_(%03d,%03d,%03d)".formatted(textColor.getColor().getRed(), textColor.getColor().getGreen(), textColor.getColor().getBlue()));
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

    private static String removeFormatCodes(String s) {
        return s.replace("§k", "").replace("§l", "").replace("§m", "").replace("§n", "").replace("§o", "").replace("§r", "");
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

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public static class TextColor {
        private static final ArrayList<TextColor> colors = new ArrayList<>();
        public static final TextColor DARK_RED = new TextColor("dark_red", '4', "\u001B[31m", new Color(170, 0, 0));
        public static final TextColor RED = new TextColor("red", 'c', "\u001B[31m", new Color(255, 85, 85));
        public static final TextColor GOLD = new TextColor("gold", '6', "\u001B[33m", new Color(255, 170, 0));
        public static final TextColor YELLOW = new TextColor("yellow", 'e', "\u001B[33m", new Color(255, 255, 85));
        public static final TextColor DARK_GREEN = new TextColor("dark_green", '2', "\u001B[32m", new Color(0, 170, 0));
        public static final TextColor GREEN = new TextColor("green", 'a', "\u001B[32m", new Color(85, 255, 85));
        public static final TextColor AQUA = new TextColor("aqua", 'b', "\u001B[36m", new Color(85, 255, 255));
        public static final TextColor DARK_AQUA = new TextColor("dark_aqua", '3', "\u001B[36m", new Color(0, 170, 170));
        public static final TextColor DARK_BLUE = new TextColor("dark_blue", '1', "\u001B[34m", new Color(0, 0, 170));
        public static final TextColor BLUE = new TextColor("blue", '9', "\u001B[34m", new Color(85, 85, 255));
        public static final TextColor LIGHT_PURPLE = new TextColor("light_purple", 'd', "\u001B[35m", new Color(255, 85, 255));
        public static final TextColor DARK_PURPLE = new TextColor("dark_purple", '5', "\u001B[35m", new Color(170, 0, 170));
        public static final TextColor WHITE = new TextColor("white", 'f', "\u001B[0m", new Color(255, 255, 255));
        public static final TextColor GRAY = new TextColor("gray", '7', "\u001B[37m", new Color(170, 170, 170));
        public static final TextColor DARK_GRAY = new TextColor("dark_gray", '8', "\u001B[37m", new Color(85, 85, 85));
        public static final TextColor BLACK = new TextColor("black", '0', "\u001B[30m", new Color(0, 0, 0));
        public static final TextColor RESET = new TextColor("reset", 'r', "\u001B[0m", new Color(255, 255, 255));

        private final String ansi, name;
        private final char char_;
        private final Color color;
        private Style style;
        public TextColor(String name, char char_, String ansi, Color color) {
            this.ansi = ansi;
            this.name = name;
            this.char_ = char_;
            this.color = color;
            values().add(this);
        }

        public static TextColor getOrAdd(String color) {
            Color c = hex2Rgb(color);
            for (TextColor value : TextColor.values()) {
                if (value.getColor() == c) {
                    return value;
                }
            }
            return new TextColor("custom", (char)0xff, "\u001B[0m", c);
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

        public static TextColor getFromColor(Color c) {
            for (TextColor value : TextColor.values()) {
                if (value.getColor().getRed() == c.getRed() && value.getColor().getGreen() == c.getGreen() && value.getColor().getBlue() == c.getBlue())
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

        public static ArrayList<TextColor> values() {
            return colors;
        }

        private static Color hex2Rgb(String colorStr) {
            return new Color(Integer.valueOf( colorStr.substring( 1, 3 ), 16 ), Integer.valueOf( colorStr.substring( 3, 5 ), 16 ), Integer.valueOf( colorStr.substring( 5, 7 ), 16 ) );
        }
    }
}
