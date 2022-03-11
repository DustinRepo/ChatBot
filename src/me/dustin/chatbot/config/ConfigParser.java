package me.dustin.chatbot.config;

import java.util.HashMap;
import java.util.Map;

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
        return configMap.get(name);
    }

    public int readInt(String name) {
        return Integer.parseInt(configMap.get(name));
    }

    public boolean readBoolean(String name) {
        return Boolean.parseBoolean(configMap.get(name));
    }

}
