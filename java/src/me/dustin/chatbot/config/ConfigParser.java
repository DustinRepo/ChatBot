package me.dustin.chatbot.config;

import me.dustin.chatbot.ChatBot;
import me.dustin.chatbot.chat.ChatMessage;
import me.dustin.chatbot.helper.GeneralHelper;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ConfigParser {

    private final Map<String, String> configMap = new HashMap<>();

    public ConfigParser(String input) {
        for (String s : input.split("\n")) {
            if (!s.startsWith("#") && s.contains("=")) {
                String valueName = s.split("=")[0];
                String value = s.split("=").length == 1 ? "" : s.split("=")[1];
                configMap.put(valueName, value);
            }
        }
    }

    public String readString(String name) {
        if (configMap.get(name) == null)
            error(name);
        return configMap.get(name);
    }

    public int readInt(String name) {
        if (configMap.get(name) == null)
            error(name);
        return Integer.parseInt(configMap.get(name));
    }

    public boolean readBoolean(String name) {
        if (configMap.get(name) == null)
            error(name);
        return Boolean.parseBoolean(configMap.get(name));
    }

    public double readDouble(String name) {
        if (configMap.get(name) == null)
            error(name);
        return Double.parseDouble(configMap.get(name));
    }

    public ArrayList<String> readStringArray(String name) {
        if (configMap.get(name) == null)
            error(name);
        ArrayList<String> strings = new ArrayList<>();
        String s = configMap.get(name);
        Pattern p = Pattern.compile("\"([^\"]*)\"");
        Matcher m = p.matcher(s);
        while (m.find()) {
            strings.add(m.group(1));
            s = s.replace("\"" + m.group(1) + "\"", "");
        }
        strings.addAll(Arrays.stream(s.split(" ")).toList());
        return strings;
    }

    private void error(String name) {
        if (ChatBot.getGui() != null)
            JOptionPane.showMessageDialog(ChatBot.getGui(), "Error! Could not find option: " + name + " in config!");
        GeneralHelper.print("Error! Could not find option: " + name + " in config!", ChatMessage.TextColor.RED);
    }
}
