package me.dustin.chatbot.config;

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
        return configMap.get(name);
    }

    public int readInt(String name) {
        return Integer.parseInt(configMap.get(name));
    }

    public boolean readBoolean(String name) {
        return Boolean.parseBoolean(configMap.get(name));
    }

    public ArrayList<String> readStringArray(String name) {
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

}
