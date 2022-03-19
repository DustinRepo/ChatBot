package me.dustin.chatbot.chat;

import com.google.gson.JsonArray;
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
    private boolean isChat;

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

    public boolean isChat() {
        return isChat;
    }

    public void setChat(boolean chat) {
        isChat = chat;
    }

    public static ChatMessage of(String jsonData) {
        try {
            if (jsonData.isEmpty())
                return new ChatMessage("", "");
            StringBuilder name = new StringBuilder();
            StringBuilder body = new StringBuilder();
            StringJoiner insertion = new StringJoiner(",");
            boolean isChat = false;
            JsonObject jsonObject = GeneralHelper.gson.fromJson(jsonData, JsonObject.class);
            JsonArray with = jsonObject.getAsJsonArray("with");
            String translate = "";
            if (jsonObject.get("translate") != null) {
                translate = jsonObject.get("translate").getAsString();
                if (translate.equalsIgnoreCase("chat.type.text"))
                    isChat = true;
            }

            if (with != null) {
                for (int i = 0; i < with.size(); i++) {
                    try {
                        JsonObject object = with.get(i).getAsJsonObject();

                        if (object.get("with") != null) {
                            JsonArray with2 = object.getAsJsonArray("with");
                            for (int ii = 0; ii < with2.size(); ii++) {
                                JsonObject o = with2.get(ii).getAsJsonObject();
                                if (o.get("insertion") != null && !insertion.toString().contains(o.get("insertion").getAsString())) {
                                    insertion.add(o.get("insertion").getAsString());
                                }
                            }
                        }
                        if (object.get("insertion") != null && insertion.toString().isEmpty()) {
                            String s = object.get("insertion").getAsString();
                            insertion.add(s);
                        }
                        if (object.get("color") != null) {
                            TextColors textColors = TextColors.getFromName(object.get("color").getAsString());
                            if (textColors != null)
                                name.append("§").append(textColors.getChar());
                        }
                        String text = object.get("text").getAsString();
                        if ((isChat || name.isEmpty()) && !Translator.translate(translate).startsWith("%1$s")) {
                            name.append(text);
                        }
                    } catch (Exception e) {
                        try {
                            body.append(" ").append(with.get(i).getAsString());
                        } catch (Exception e1) {
                        }
                    }
                }
            }
            if (jsonObject.get("translate") != null) {
                if (jsonObject.get("color") != null) {
                    TextColors textColors = TextColors.getFromName(jsonObject.get("color").getAsString());
                    if (textColors != null)
                        body.append("§").append(textColors.getChar());
                }
                if (!translate.equalsIgnoreCase("chat.type.text")) {
                    String translated = Translator.translate(translate);
                    if (!insertion.toString().isEmpty()) {
                        String[] insertions = insertion.toString().split(",");
                        int c = 0;
                        for (int i = 0; i < insertions.length; i++) {
                            if (translated.contains("%" + (i + 1) + "$s")) {
                                translated = translated.replace("%" + (i + 1) + "$s", insertions[i]);
                                c++;
                            } else
                                break;
                        }
                        for (int i = c; i < insertions.length; i++) {
                            translated = translated.replaceFirst("%s", insertions[i]);
                        }
                    }
                    //translated = translated.replace("%s", insertions[insertions.length - 1]);
                    body.append(" ").append(translated);
                }
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
            ChatMessage chatMessage = new ChatMessage(name.toString(), body.toString());
            chatMessage.setChat(isChat);
            return chatMessage;
        } catch (Exception e) {
            return new ChatMessage("", jsonData);
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
                        TextColors textColors = TextColors.getFromName(object.get("color").getAsString());
                        if (textColors != null)
                            s.append("§").append(textColors.getChar());
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
                            TextColors textColors = TextColors.getFromName(o.get("color").getAsString());
                            if (textColors != null)
                                s.append("§").append(textColors.getChar());
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

    public enum TextColors {
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
        BLACK("black", '0', "\u001B[30m", new Color(0, 0, 0));


        private final String ansi, name;
        private final char char_;
        private final Color color;
        private Style style;
        TextColors(String name, char char_, String ansi, Color color) {
            this.ansi = ansi;
            this.name = name;
            this.char_ = char_;
            this.color = color;
        }

        public static TextColors getFromName(String name) {
            for (TextColors value : TextColors.values()) {
                if (value.getName().equalsIgnoreCase(name))
                    return value;
            }
            return null;
        }

        public static TextColors getFromChar(char char_) {
            for (TextColors value : TextColors.values()) {
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
