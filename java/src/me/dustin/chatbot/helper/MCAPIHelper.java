package me.dustin.chatbot.helper;

import com.google.common.collect.Maps;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MCAPIHelper {

    private final static String NAME_API_URL = "https://api.mojang.com/users/profiles/minecraft/%s";
    private final static String UUID_API_URL = "https://api.mojang.com/user/profiles/%s/names";

    private final static Map<String, UUID> nameMap = Maps.newHashMap();
    private final static Map<UUID, String> uuidMap = Maps.newHashMap();

    public static Map<String, Long> getNameHistory(UUID uuid) {
        Map<String, Long> names = new HashMap<>();
        try {
            String result = GeneralHelper.httpRequest(String.format(UUID_API_URL, uuid.toString().replace("-", "")), null, null,"GET").data();
            JsonArray nameArray = GeneralHelper.gson.fromJson(result, JsonArray.class);
            for (int i = 0; i < nameArray.size(); i++) {
                JsonObject object = nameArray.get(i).getAsJsonObject();
                String name = object.get("name").getAsString();
                long changedToAt = -1;
                if (object.get("changedToAt") != null) {
                    changedToAt = object.get("changedToAt").getAsLong();
                }
                names.put(name, changedToAt);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return names;
    }

    public static String getLastChangedName(UUID uuid) {
        try {
            String result = GeneralHelper.httpRequest(String.format(UUID_API_URL, uuid.toString().replace("-", "")), null, null, "GET").data();
            JsonArray nameArray = GeneralHelper.gson.fromJson(result, JsonArray.class);
            JsonObject object = nameArray.get(nameArray.size() - 2).getAsJsonObject();
            return object.get("name").getAsString();
        } catch (Exception e) {
            return null;
        }
    }

    public static String getNameFromUUID(UUID uuid) {
        if (uuid == null)
            return "";
        if (uuidMap.containsKey(uuid))
            return uuidMap.get(uuid);
        String result = GeneralHelper.httpRequest(String.format(UUID_API_URL, uuid.toString().replace("-", "")), null, null, "GET").data();
        if (result == null)
            return "";
        JsonArray nameArray = GeneralHelper.gson.fromJson(result, JsonArray.class);
        try {
            JsonObject object = nameArray.get(nameArray.size() - 1).getAsJsonObject();

            String name = object.get("name").getAsString();
            uuidMap.putIfAbsent(uuid, name);
            return name;
        } catch (Exception e) {
            return "";
        }
    }

    public static UUID getUUIDFromName(String name) {
        try {
            if (nameMap.containsKey(name))
                return nameMap.get(name);
            String result = GeneralHelper.httpRequest(String.format(NAME_API_URL, name), null, null, "GET").data();
            if (result == null)
                return null;
            JsonObject object =  GeneralHelper.gson.fromJson(result, JsonObject.class);
            UUID uuid = UUID.fromString(object.get("id").getAsString().replaceFirst(
                    "(\\p{XDigit}{8})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}+)", "$1-$2-$3-$4-$5"
            ));
            nameMap.putIfAbsent(name, uuid);
            return uuid;
        } catch (Exception e) {
            return null;
        }
    }

}
