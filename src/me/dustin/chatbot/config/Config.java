package me.dustin.chatbot.config;

import me.dustin.chatbot.helper.GeneralHelper;

import java.io.File;
import java.io.IOException;

public class Config {


    private final String commandPrefix;
    private final String accountType;
    private final String crackedLoginPassword;

    private final boolean greenText;
    private final boolean colorConsole;
    private final boolean crackedLogin;
    private final boolean reconnect;

    private final int protocolVersion;
    private final int reconnectDelay;
    private final int messageDelay;
    private final int announcementDelay;
    private final int keepAliveCheckTime;

    private final File loginFile;

    public Config(File file) throws IOException {
        ConfigParser parser = new ConfigParser(GeneralHelper.readFile(file));

        commandPrefix = parser.readString("commandPrefix");
        accountType = parser.readString("accountType");
        crackedLoginPassword = parser.readString("crackedLoginPassword");
        colorConsole = parser.readBoolean("consoleColor");
        crackedLogin = parser.readBoolean("crackedLogin");
        greenText = parser.readBoolean("greenText");
        reconnect = parser.readBoolean("reconnect");

        protocolVersion = parser.readInt("protocolVersion");
        reconnectDelay = parser.readInt("reconnectDelay");
        messageDelay = parser.readInt("messageDelay");
        announcementDelay = parser.readInt("announcementDelay");
        keepAliveCheckTime = parser.readInt("keepAliveCheckTime");

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

    public File getLoginFile() {
        return loginFile;
    }
}
