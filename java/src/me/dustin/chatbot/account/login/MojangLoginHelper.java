package me.dustin.chatbot.account.login;

import com.google.gson.JsonObject;
import me.dustin.chatbot.account.MinecraftAccount;
import me.dustin.chatbot.account.Session;
import me.dustin.chatbot.helper.GeneralHelper;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MojangLoginHelper {

    private static final String AUTHENTICATE_URL = "https://authserver.mojang.com/authenticate";
    private String email, password;
    private boolean cracked;

    public MojangLoginHelper(String email, String password) {
        this.email = email;
        this.password = password;
        this.cracked = !email.contains("@");
    }

    public MojangLoginHelper(MinecraftAccount.MojangAccount mojangAccount) {
        this.email = mojangAccount.getEmail();
        this.password = mojangAccount.getPassword();
        this.cracked = mojangAccount.isCracked();
    }

    public Session login() {
        if (cracked)
            return new Session(email, UUID.randomUUID().toString(), "fakeToken", Session.AccountType.MOJANG);
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("agent", "Minecraft");
        jsonObject.addProperty("username", email);
        jsonObject.addProperty("password", password);
        jsonObject.addProperty("requestUser", true);
        Map<String, String> header = new HashMap<>();
        header.put("Content-Type", "application/json");
        String resp = GeneralHelper.httpRequest(AUTHENTICATE_URL, jsonObject.toString(), header, "POST").data();

        if (resp != null && !resp.isEmpty()) {
            JsonObject object = GeneralHelper.prettyGson.fromJson(resp, JsonObject.class);
            JsonObject selectedProfile = object.get("selectedProfile").getAsJsonObject();
            String name = selectedProfile.get("name").getAsString();
            String uuid = selectedProfile.get("id").getAsString();
            String accessToken = object.get("accessToken").getAsString();
            return new Session(name, uuid, accessToken, Session.AccountType.MOJANG);
        }
        return null;
    }

}
