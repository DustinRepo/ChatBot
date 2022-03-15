package me.dustin.chatbot.account;

import me.dustin.chatbot.account.login.MSLoginHelper;
import me.dustin.chatbot.account.login.MojangLoginHelper;
import me.dustin.chatbot.chat.ChatMessage;
import me.dustin.chatbot.helper.GeneralHelper;

public class MinecraftAccount {
    protected String email, password;

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

    public Session login() {
        if (this instanceof MicrosoftAccount microsoftAccount) {
            GeneralHelper.print("Logging in to Microsoft account...", ChatMessage.TextColors.YELLOW);
            MSLoginHelper msLoginHelper = new MSLoginHelper(microsoftAccount);
            return msLoginHelper.login(s -> GeneralHelper.print(s, ChatMessage.TextColors.YELLOW));
        } else {
            GeneralHelper.print("Logging in to Mojang account...", ChatMessage.TextColors.YELLOW);
            MojangAccount mojangAccount = (MojangAccount)this;
            MojangLoginHelper mojangLoginHelper = new MojangLoginHelper(mojangAccount);
            return mojangLoginHelper.login();
        }
    }
}
