package me.dustin.chatbot.config;

import me.dustin.chatbot.helper.GeneralHelper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class Config {

    private final File configFile;

    private ArrayList<String> loginKeywords;
    private ArrayList<String> passwordKeywords;
    private String proxyString;
    private String proxyUsername;
    private String proxyPassword;
    private String commandPrefix;
    private String accountType;
    private String crackedLoginPassword;
    private String locale;
    private String loginCommand;
    private String passwordCreateCommand;

    private boolean announcements;
    private boolean greenText;
    private boolean colorConsole;
    private boolean crackedLogin;
    private boolean reconnect;
    private boolean antiAFK;
    private boolean allowServerListing;
    private boolean skinBlink;
    private boolean passwordCreateUseTwice;

    private int protocolVersion;
    private int reconnectDelay;
    private int messageDelay;
    private int announcementDelay;
    private int antiAFKDelay;
    private int keepAliveCheckTime;
    private int skinBlinkDelay;

    private File loginFile;

    public Config(File file) throws IOException{
        this.configFile = file;
        loadConfig();
    }

    public void loadConfig() throws IOException {
        ConfigParser parser = new ConfigParser(GeneralHelper.readFile(configFile));
        loginKeywords = parser.readStringArray("loginKeywords");
        passwordKeywords = parser.readStringArray("passwordKeywords");
        proxyString = parser.readString("proxy");
        proxyUsername = parser.readString("proxyUsername");
        proxyPassword = parser.readString("proxyPassword");
        commandPrefix = parser.readString("commandPrefix");
        accountType = parser.readString("accountType");
        crackedLoginPassword = parser.readString("crackedLoginPassword");
        locale = parser.readString("locale");
        loginCommand = parser.readString("loginCommand");
        passwordCreateCommand = parser.readString("passwordCreateCommand");

        colorConsole = parser.readBoolean("consoleColor");
        crackedLogin = parser.readBoolean("crackedLogin");
        announcements = parser.readBoolean("announcements");
        greenText = parser.readBoolean("greenText");
        reconnect = parser.readBoolean("reconnect");
        antiAFK = parser.readBoolean("antiAFK");
        allowServerListing = parser.readBoolean("allowServerListing");
        skinBlink = parser.readBoolean("skinBlink");
        passwordCreateUseTwice = parser.readBoolean("passwordCreateUseTwice");

        protocolVersion = parser.readInt("protocolVersion");
        reconnectDelay = parser.readInt("reconnectDelay");
        messageDelay = parser.readInt("messageDelay");
        announcementDelay = parser.readInt("announcementDelay");
        keepAliveCheckTime = parser.readInt("keepAliveCheckTime");
        antiAFKDelay = parser.readInt("antiAFKDelay");
        skinBlinkDelay = parser.readInt("skinBlinkDelay");

        String jarPath = new File("").getAbsolutePath();
        loginFile = new File(jarPath, parser.readString("loginFile"));
    }

    public File getConfigFile() {
        return configFile;
    }

    public ArrayList<String> getLoginKeywords() {
        return loginKeywords;
    }

    public ArrayList<String> getPasswordKeywords() {
        return passwordKeywords;
    }

    public String getProxyString() {
        return proxyString;
    }

    public String getProxyUsername() {
        return proxyUsername;
    }

    public String getProxyPassword() {
        return proxyPassword;
    }

    public String getCommandPrefix() {
        return commandPrefix;
    }

    public String getAccountType() {
        return accountType;
    }

    public String getCrackedLoginPassword() {
        return crackedLoginPassword;
    }

    public String getLocale() {
        return locale;
    }

    public String getLoginCommand() {
        return loginCommand;
    }

    public String getPasswordCreateCommand() {
        return passwordCreateCommand;
    }

    public boolean isAnnouncements() {
        return announcements;
    }

    public boolean isGreenText() {
        return greenText;
    }

    public boolean isColorConsole() {
        return colorConsole;
    }

    public boolean isCrackedLogin() {
        return crackedLogin;
    }

    public boolean isReconnect() {
        return reconnect;
    }

    public boolean isAntiAFK() {
        return antiAFK;
    }

    public boolean isAllowServerListing() {
        return allowServerListing;
    }

    public boolean isSkinBlink() {
        return skinBlink;
    }

    public boolean isPasswordCreateUseTwice() {
        return passwordCreateUseTwice;
    }

    public int getProtocolVersion() {
        return protocolVersion;
    }

    public int getReconnectDelay() {
        return reconnectDelay;
    }

    public int getMessageDelay() {
        return messageDelay;
    }

    public int getAnnouncementDelay() {
        return announcementDelay;
    }

    public int getAntiAFKDelay() {
        return antiAFKDelay;
    }

    public int getKeepAliveCheckTime() {
        return keepAliveCheckTime;
    }

    public int getSkinBlinkDelay() {
        return skinBlinkDelay;
    }

    public File getLoginFile() {
        return loginFile;
    }
}
