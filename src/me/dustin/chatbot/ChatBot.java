package me.dustin.chatbot;

import me.dustin.chatbot.account.MinecraftAccount;
import me.dustin.chatbot.account.Session;
import me.dustin.chatbot.config.Config;
import me.dustin.chatbot.helper.GeneralHelper;
import me.dustin.chatbot.network.ClientConnection;

import java.io.File;
import java.io.IOException;

public class ChatBot {

    private static Config config;
    private static ClientConnection clientConnection;

    public static void main(String[] args) throws IOException, InterruptedException {
        String jarPath = new File("").getAbsolutePath();
        config = new Config(new File(jarPath, "config.cfg"));
        if (args.length < 1) {
            GeneralHelper.print("ERROR: No IP specified in arguments!", GeneralHelper.ANSI_RED);
            return;
        }
        String ip = args[0];
        int port = 25565;
        if (ip.contains(":")) {
            port = Integer.parseInt(ip.split(":")[1]);
            ip = ip.split(":")[0];
        }
        File loginFile = config.getLoginFile();
        if (!loginFile.exists()) {
            GeneralHelper.print("ERROR: No login file!", GeneralHelper.ANSI_RED);
            return;
        }

        String[] loginInfo = GeneralHelper.readFile(loginFile).split("\n");
        MinecraftAccount minecraftAccount;
        switch (config.getAccountType()) {
            case "MSA" -> minecraftAccount = new MinecraftAccount.MicrosoftAccount(loginInfo[0], loginInfo[1]);
            case "MOJ" -> minecraftAccount = loginInfo.length > 1 ? new MinecraftAccount.MojangAccount(loginInfo[0], loginInfo[1]) : new MinecraftAccount.MojangAccount(loginInfo[0]);
            default -> {
                GeneralHelper.print("ERROR: Unknown account type in config!", GeneralHelper.ANSI_RED);
                return;
            }
        }
        Session session = minecraftAccount.login();
        if (session == null) {
            GeneralHelper.print("ERROR: Login failed!", GeneralHelper.ANSI_RED);
            return;
        }
        GeneralHelper.print("Logged in. Starting connection to " + args[0], GeneralHelper.ANSI_GREEN);

        connectionLoop(ip, port, session);

        GeneralHelper.print("Connection closed.", GeneralHelper.ANSI_RED);
    }

    private static void connectionLoop(String ip, int port, Session session) throws InterruptedException, IOException {
        clientConnection = new ClientConnection(ip, port, session);
        clientConnection.connect();

        while (clientConnection.isConnected()) {
            clientConnection.tick();
        }
        if (getConfig().isReconnect()) {
            GeneralHelper.print("Client disconnected, reconnecting in " + getConfig().getReconnectDelay() + " seconds...", GeneralHelper.ANSI_PURPLE);
            Thread.sleep(getConfig().getReconnectDelay() * 1000L);
            connectionLoop(ip, port, session);
        }
    }

    public static Config getConfig() {
        return config;
    }

    public static ClientConnection getClientConnection() {
        return clientConnection;
    }
}
