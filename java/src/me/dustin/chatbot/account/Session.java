package me.dustin.chatbot.account;

public class Session {

    private final String username;
    private final String uuid;
    private final String accessToken;
    private final AccountType accountType;

    public Session(String username, String uuid, String accessToken, AccountType accountType) {
        this.username = username;
        this.uuid = uuid;
        this.accessToken = accessToken;
        this.accountType = accountType;
    }

    public enum AccountType {
        LEGACY("legacy"),
        MOJANG("mojang"),
        MICROSOFT("msa"),
        ALTENING("altening");

        public final String name;

        AccountType(String name) {
            this.name = name;
        }
    }

    public String getUsername() {
        return username;
    }

    public String getUuid() {
        return uuid;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public AccountType getAccountType() {
        return accountType;
    }
}
