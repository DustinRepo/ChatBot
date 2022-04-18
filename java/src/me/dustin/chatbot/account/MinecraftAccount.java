package me.dustin.chatbot.account;

import com.google.gson.JsonObject;
import me.dustin.chatbot.account.login.MSLoginHelper;
import me.dustin.chatbot.account.login.MojangLoginHelper;
import me.dustin.chatbot.chat.ChatMessage;
import me.dustin.chatbot.helper.GeneralHelper;

import java.util.HashMap;
import java.util.Map;

public class MinecraftAccount {
    protected String email, password;
    private boolean loginAgain;

    public static class TheAlteningAccount extends MinecraftAccount {
        public TheAlteningAccount(String token) {
            this.email = token;
        }
    }

    public static class MicrosoftAccount extends MinecraftAccount {
        public MicrosoftAccount(String email, String password) {
            this.email = email;
            this.password = password;
        }
    }

    public static class MojangAccount extends MinecraftAccount {
        private boolean isCracked;

        public MojangAccount(String username) {
            this.email = username;
            this.password = "";
            this.isCracked = true;
        }

        public MojangAccount(String email, String password) {
            this.email = email;
            this.password = password;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public boolean isCracked() {
            return isCracked;
        }

        public void setCracked(boolean cracked) {
            isCracked = cracked;
        }
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isLoginAgain() {
        return loginAgain;
    }

    public void setLoginAgain(boolean loginAgain) {
        this.loginAgain = loginAgain;
    }

    public Session login() {
        if (this instanceof MicrosoftAccount microsoftAccount) {
            GeneralHelper.print("Logging in to Microsoft account...", ChatMessage.TextColor.YELLOW);
            MSLoginHelper msLoginHelper = new MSLoginHelper(microsoftAccount);
            return msLoginHelper.login(s -> {
            });
        } else if (this instanceof MojangAccount mojangAccount) {
            GeneralHelper.print("Logging in to Mojang account...", ChatMessage.TextColor.YELLOW);
            MojangLoginHelper mojangLoginHelper = new MojangLoginHelper(mojangAccount);
            return mojangLoginHelper.login();
        } else {
            GeneralHelper.print("Logging in to TheAltening account...", ChatMessage.TextColor.YELLOW);
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("agent", "Minecraft");
            jsonObject.addProperty("username", email);
            jsonObject.addProperty("password", "ChatBot");
            jsonObject.addProperty("requestUser", true);
            Map<String, String> header = new HashMap<>();
            header.put("Content-Type", "application/json");
            String resp = GeneralHelper.httpRequest("http://authserver.thealtening.com/authenticate", jsonObject.toString(), header, "POST").data();

            if (resp != null && !resp.isEmpty()) {
                JsonObject object = GeneralHelper.prettyGson.fromJson(resp, JsonObject.class);
                JsonObject selectedProfile = object.get("selectedProfile").getAsJsonObject();
                String name = selectedProfile.get("name").getAsString();
                String uuid = selectedProfile.get("id").getAsString();
                String accessToken = object.get("accessToken").getAsString();
                return new Session(name, uuid, accessToken, Session.AccountType.ALTENING);
            }
            return null;
        }
    }
}
