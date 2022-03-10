package me.dustin.chatbot.config;

import me.dustin.chatbot.helper.GeneralHelper;

import java.io.File;
import java.io.IOException;

public class Config {

    private final File configFile;

    private String commandPrefix;
    private String accountType;
    private String crackedLoginPassword;

    private boolean greenText;
    private boolean colorConsole;
    private boolean crackedLogin;
    private boolean reconnect;
    private boolean antiAFK;

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
        colorConsole = parser.readBoolean("consoleColor");
        crackedLogin = parser.readBoolean("crackedLogin");
        greenText = parser.readBoolean("greenText");
        reconnect = parser.readBoolean("reconnect");
        antiAFK = parser.readBoolean("antiAFK");

        protocolVersion = parser.readInt("protocolVersion");
        reconnectDelay = parser.readInt("reconnectDelay");
        messageDelay = parser.readInt("messageDelay");
        announcementDelay = parser.readInt("announcementDelay");
        keepAliveCheckTime = parser.readInt("keepAliveCheckTime");
        antiAFKDelay = parser.readInt("antiAFKDelay");

        String jarPath = new File("").getAbsolutePath();
        loginFile = new File(jarPath, parser.readString("loginFile"));
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

    public int getKeepAliveCheckTime() {
        return keepAliveCheckTime;
    }

    public boolean isAntiAFK() {
        return antiAFK;
    }

    public int getAntiAFKDelay() {
        return antiAFKDelay;
    }

    public File getLoginFile() {
        return loginFile;
    }
}
