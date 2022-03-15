package me.dustin.chatbot.network;

import me.dustin.chatbot.ChatBot;

public enum Protocols {
    V1_18_2("1.18.2", 758),
    V1_18("1.18", 757),
    V1_17_1("1.17.1", 756),
    V1_17("1.17", 755),
    V1_16_5("1.16.5", 754),
    V1_16_3("1.16.3", 753),
    V1_16_2("1.16.2", 751),
    V1_16_1("1.16.1", 736),
    V1_16("1.16", 735),
    V1_15_2("1.15.2", 578),
    V1_15_1("1.15.1", 575),
    V1_15("1.15", 573),
    V1_14_4("1.14.4", 498),
    V1_14_3("1.14.3", 490),
    V1_14_1("1.14.1", 480),
    V1_14("1.14", 477),
    V1_13_2("1.13.2", 404),
    V1_13_1("1.13.1", 401),
    V1_13("1.13", 393),
    V1_12_2("1.12.2", 340),
    V1_12_1("1.12.1", 338),
    V1_12("1.12", 335);
    private final String name;
    private final int protocolVer;
    private static Protocols current;
    Protocols(String name, int protocolVer) {
        this.name = name;
        this.protocolVer = protocolVer;
    }

    public static Protocols get(String name) {
        for (Protocols value : Protocols.values()) {
            if (value.getName().equalsIgnoreCase(name))
                return value;
        }
        return V1_18_2;
    }

    public static Protocols getCurrent() {
        if (current == null)
            current = get(ChatBot.getConfig().getClientVersion());
        return current;
    }

    public String getName() {
        return name;
    }

    public int getProtocolVer() {
        return protocolVer;
    }
}
