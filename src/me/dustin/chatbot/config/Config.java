package me.dustin.chatbot.config;

import me.dustin.chatbot.helper.GeneralHelper;

import java.io.File;
import java.io.IOException;

public class Config {

    private final File configFile;

    private String commandPrefix;
    private String accountType;
    private String crackedLoginPassword;
    private String locale;

    private boolean greenText;
    private boolean colorConsole;
    private boolean crackedLogin;
    private boolean reconnect;
    private boolean antiAFK;
    private boolean allowServerListing;

    private int protocolVersion;
    private int reconnectDelay;
    private int messageDelay;
    private int announcementDelay;
    private int antiAFKDelay;
    private int keepAliveCheckTime;

    private File loginFile;

    public Config(File file) throws IOException{
        this.configFile = file;
        loadConfig();
    }

    public void loadConfig() throws IOException {
        ConfigParser parser = new ConfigParser(GeneralHelper.readFile(configFile));
        commandPrefix = parser.readString("commandPrefix");
        accountType = parser.readString("accountType");
        crackedLoginPassword = parser.readString("crackedLoginPassword");
        locale = parser.readString("locale");

        colorConsole = parser.readBoolean("consoleColor");
        crackedLogin = parser.readBoolean("crackedLogin");
        greenText = parser.readBoolean("greenText");
        reconnect = parser.readBoolean("reconnect");
        antiAFK = parser.readBoolean("antiAFK");
        allowServerListing = parser.readBoolean("allowServerListing");

        protocolVersion = parser.readInt("protocolVersion");
        reconnectDelay = parser.readInt("reconnectDelay");
        messageDelay = parser.readInt("messageDelay");
        announcementDelay = parser.readInt("announcementDelay");
        keepAliveCheckTime = parser.readInt("keepAliveCheckTime");
        antiAFKDelay = parser.readInt("antiAFKDelay");

        String jarPath = new File("").getAbsolutePath();
        loginFile = new File(jarPath, parser.readString("loginFile"));
    }

    public File getConfigFile() {
        return configFile;
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

    public File getLoginFile() {
        return loginFile;
    }
}
