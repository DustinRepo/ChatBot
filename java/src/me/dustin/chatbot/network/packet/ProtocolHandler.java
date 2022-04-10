package me.dustin.chatbot.network.packet;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import me.dustin.chatbot.ChatBot;
import me.dustin.chatbot.chat.ChatMessage;
import me.dustin.chatbot.helper.GeneralHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

public class ProtocolHandler {

    private static ProtocolVersion current;
    private static final ArrayList<ProtocolVersion> versions = new ArrayList<>();

    public static void downloadData() {
        String data = GeneralHelper.httpRequest("https://gitlab.bixilon.de/bixilon/minosoft/-/raw/master/src/main/resources/assets/minosoft/mapping/versions.json", null, null, "GET").data();
        JsonObject jsonObject = GeneralHelper.prettyGson.fromJson(data, JsonObject.class);
        for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
            int redirectId = -1;
            ArrayList<String> c2sList = new ArrayList<>();
            ArrayList<String> s2cList = new ArrayList<>();
            JsonObject protocolObj = (JsonObject) entry.getValue();
            String name = protocolObj.get("name").getAsString();
            int id = Integer.parseInt(entry.getKey());
            int protocolId;
            if (!protocolObj.has("protocol_id"))
                protocolId = id;
            else
                protocolId = protocolObj.get("protocol_id").getAsInt();

            JsonElement element = protocolObj.get("packets");
            try {
                redirectId = element.getAsInt();
            } catch (Exception ex) {
                JsonObject packetsObj = (JsonObject) element;
                JsonArray c2s;
                JsonArray s2c;
                try {
                    c2s = packetsObj.getAsJsonArray("c2s");
                    s2c = packetsObj.getAsJsonArray("s2c");
                } catch (Exception e) {
                    JsonObject c2sObj = packetsObj.getAsJsonObject("c2s");
                    JsonObject s2cObj = packetsObj.getAsJsonObject("s2c");
                    c2s = c2sObj.getAsJsonArray("play");
                    s2c = s2cObj.getAsJsonArray("play");
                }
                for (JsonElement e : c2s) {
                    c2sList.add(e.getAsString());
                }
                for (JsonElement e : s2c) {
                    s2cList.add(e.getAsString());
                }
            }

            versions.add(redirectId != -1 ? new ProtocolVersion(name, id, protocolId, redirectId) : new ProtocolVersion(name, id, protocolId, c2sList, s2cList));
        }
        setCurrent(getVersionFromName(ChatBot.getConfig().getClientVersion()).getProtocolVer());
    }

    public static ProtocolVersion getCurrent() {
        return current;
    }

    public static void setCurrent(int protocolId) {
        current = getVersionFromProtocol(protocolId);
        ChatBot.getConfig().setProtocolVersion(protocolId);
    }

    public static ProtocolVersion getVersion(int id) {
        for (ProtocolVersion version : versions) {
            if (version.id == id)
                return version;
        }
        return versions.get(0);
    }

    public static ProtocolVersion getVersionFromName(String name) {
        for (ProtocolVersion version : versions) {
            if (version.name.equalsIgnoreCase(name))
                return version;
        }
        return versions.get(0);
    }

    public static ProtocolVersion getVersionFromProtocol(int protocolId) {
        for (ProtocolVersion version : versions) {
            if (version.protocolId == protocolId)
                return version;
        }
        return versions.get(0);
    }

    public static class ProtocolVersion {
        private final String name;
        private final int id;
        private final int protocolId;
        private final int redirectId;
        private final ArrayList<String> c2sList;
        private final ArrayList<String> s2cList;

        public ProtocolVersion(String name, int id, int protocolId, ArrayList<String> c2sList, ArrayList<String> s2cList) {
            this.name = name;
            this.id = id;
            this.protocolId = protocolId;
            this.c2sList = c2sList;
            this.s2cList = s2cList;
            this.redirectId = -1;
        }

        public ProtocolVersion(String name, int id, int protocolId, int redirectId) {
            this.name = name;
            this.id = id;
            this.protocolId = protocolId;
            this.redirectId = redirectId;
            this.c2sList = new ArrayList<>();
            this.s2cList = new ArrayList<>();
        }

        public int getPacketId(NetworkSide networkSide, String... names) {
            if (redirectId != -1 && (c2sList.isEmpty() || s2cList.isEmpty())) {
                ProtocolVersion v = ProtocolHandler.getVersion(redirectId);
                if (v == null) {
                    throw new RuntimeException("Could not find packets for protocol version!");
                }
                c2sList.addAll(v.c2sList);
                s2cList.addAll(v.s2cList);
            }
            switch (networkSide) {
                case SERVERBOUND -> {
                    for (String name : names) {
                        if (c2sList.contains(name))
                            return c2sList.indexOf(name);
                    }
                }
                case CLIENTBOUND -> {
                    for (String name : names) {
                        if (s2cList.contains(name))
                            return s2cList.indexOf(name);
                    }
                }
            }
            return -1;
        }

        public int getProtocolVer() {
            return protocolId;
        }

        public String getName() {
            return name;
        }
    }

    public static enum NetworkSide {
        CLIENTBOUND, SERVERBOUND
    }
}
