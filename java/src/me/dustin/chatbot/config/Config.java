package me.dustin.chatbot.config;

import me.dustin.chatbot.helper.GeneralHelper;
import me.dustin.chatbot.network.Protocols;

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
    private String clientVersion;
    private String commandPrefix;
    private String accountType;
    private String crackedLoginPassword;
    private String locale;
    private String loginCommand;
    private String passwordCreateCommand;

    private boolean commands;
    private boolean log;
    private boolean announcements;
    private boolean greenText;
    private boolean colorConsole;
    private boolean crackedLogin;
    private boolean reconnect;
    private boolean antiAFK;
    private boolean allowServerListing;
    private boolean skinBlink;
    private boolean passwordCreateUseTwice;
    private boolean twoB2tCheck;
    private boolean twoB2tCount;
    private boolean quotes;
    private boolean numberCount;
    private boolean repeatMessages;

    private int protocolVersion;
    private int proxySOCKS;
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

    public void loadConfig() {
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
        clientVersion = parser.readString("clientVersion");
        protocolVersion = Protocols.get(clientVersion).getProtocolVer();

        commands = parser.readBoolean("commands");
        log = parser.readBoolean("log");
        colorConsole = parser.readBoolean("consoleColor");
        crackedLogin = parser.readBoolean("crackedLogin");
        announcements = parser.readBoolean("announcements");
        greenText = parser.readBoolean("greenText");
        reconnect = parser.readBoolean("reconnect");
        antiAFK = parser.readBoolean("antiAFK");
        allowServerListing = parser.readBoolean("allowServerListing");
        skinBlink = parser.readBoolean("skinBlink");
        passwordCreateUseTwice = parser.readBoolean("passwordCreateUseTwice");
        twoB2tCheck = parser.readBoolean("2b2tCheck");
        twoB2tCount = parser.readBoolean("2b2tCount");
        quotes = parser.readBoolean("quotes");
        numberCount = parser.readBoolean("numberCount");
        repeatMessages = parser.readBoolean("repeatMessages");

        reconnectDelay = parser.readInt("reconnectDelay");
        messageDelay = parser.readInt("messageDelay");
        announcementDelay = parser.readInt("announcementDelay");
        keepAliveCheckTime = parser.readInt("keepAliveCheckTime");
        antiAFKDelay = parser.readInt("antiAFKDelay");
        skinBlinkDelay = parser.readInt("skinBlinkDelay");
        proxySOCKS = parser.readInt("socks");

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

    public String getClientVersion() {
        return clientVersion;
    }

    public boolean isCommands() {
        return commands;
    }

    public boolean isLog() {
        return log;
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

    public boolean is2b2tCheck() {
        return twoB2tCheck;
    }

    public boolean is2b2tCount() {
        return twoB2tCount;
    }

    public boolean isQuotes() {
        return quotes;
    }

    public boolean isNumberCount() {
        return numberCount;
    }

    public boolean isPasswordCreateUseTwice() {
        return passwordCreateUseTwice;
    }

    public boolean isRepeatMessages() {
        return repeatMessages;
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

    public int getProxySOCKS() {
        return proxySOCKS;
    }

    public File getLoginFile() {
        return loginFile;
    }
}
