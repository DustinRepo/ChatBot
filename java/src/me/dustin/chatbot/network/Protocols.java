package me.dustin.chatbot.network;

import me.dustin.chatbot.ChatBot;

public enum Protocols {
    V1_18_2(758, "1.18.2"),
    V1_18(757, "1.18.1", "1.18"),
    V1_17_1(756, "1.17.1"),
    V1_17(755, "1.17"),
    V1_16_5(754, "1.16.5", "1.16.4"),
    V1_16_3(753, "1.16.3"),
    V1_16_2(751, "1.16.2"),
    V1_16_1(736, "1.16.1"),
    V1_16(735, "1.16"),
    V1_15_2(578, "1.15.2"),
    V1_15_1(575, "1.15.1"),
    V1_15(573, "1.15"),
    V1_14_4(498, "1.14.4"),
    V1_14_3(490, "1.14.3"),
    V1_14_2(485, "1.14.2"),
    V1_14_1(480, "1.14.1"),
    V1_14(477, "1.14"),
    V1_13_2(404, "1.13.2"),
    V1_13_1(401, "1.13.1"),
    V1_13(393, "1.13"),
    V1_12_2(340, "1.12.2"),
    V1_12_1(338, "1.12.1"),
    V1_12(335, "1.12"),
    V1_11_2(316, "1.11.1", "1.11.2"),
    V1_11(315, "1.11"),
    V1_10(210, "1.10", "1.10.1", "1.10.2"),
    V1_9_4(110, "1.9.4", "1.9.3"),
    V1_9_2(109, "1.9.2"),
    V1_9_1(108, "1.9.1"),
    V1_9(107, "1.9"),
    V1_8(47, "1.8", "1.8.1", "1.8.2", "1.8.3", "1.8.4", "1.8.5", "1.8.6", "1.8.7", "1.8.8", "1.8.9"),
    V1_7_10(5, "1.7.10"),
    V1_7_2(4, "1.7.2", "1.7.4", "1.7.5");
    private final String[] names;
    private final int protocolVer;
    private static Protocols current;
    Protocols(int protocolVer, String... names) {
        this.names = names;
        this.protocolVer = protocolVer;
    }

    public static Protocols get(String name) {
        for (Protocols value : Protocols.values()) {
            for (String valueName : value.getNames()) {
                if (valueName.equalsIgnoreCase(name))
                    return value;
            }
        }
        return Protocols.values()[0];
    }

    public static Protocols get(int v) {
        for (Protocols value : Protocols.values()) {
            if (value.getProtocolVer() == v)
                return value;
        }
        return Protocols.values()[0];
    }

    public static Protocols getCurrent() {
        if (current == null) {
            current = get(ChatBot.getConfig().getClientVersion());
        }
        return current;
    }

    public String[] getNames() {
        return names;
    }

    public int getProtocolVer() {
        return protocolVer;
    }
}
