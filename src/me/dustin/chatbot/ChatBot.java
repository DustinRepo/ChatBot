package me.dustin.chatbot;

import me.dustin.chatbot.account.MinecraftAccount;
import me.dustin.chatbot.account.Session;
import me.dustin.chatbot.config.Config;
import me.dustin.chatbot.gui.ChatBotGui;
import me.dustin.chatbot.helper.GeneralHelper;
import me.dustin.chatbot.helper.Timer;
import me.dustin.chatbot.network.ClientConnection;

import javax.swing.*;
import java.io.File;
import java.io.IOException;

public class ChatBot {

    private static Config config;
    private static ClientConnection clientConnection;
    private static ChatBotGui gui;
    private static Timer timer = new Timer();

    public static void main(String[] args) throws IOException, InterruptedException {
        String jarPath = new File("").getAbsolutePath();
        config = new Config(new File(jarPath, "config.cfg"));
        String ip = null;

        boolean noGui = false;
        if (args.length > 0)
        for (String arg : args) {
            if (arg.equalsIgnoreCase("--nogui")) {
                noGui = true;
            } else if (arg.startsWith("--ip=")) {
                ip = arg.split("=")[1];
            }
        }

        if (ip == null) {
            if (noGui) {
                GeneralHelper.print("ERROR: No IP specified in arguments! Use --ip=<ip:port>!", GeneralHelper.TextColors.RED);
                return;
            } else {
                ip = JOptionPane.showInputDialog("Input ip or ip:port");
                if (ip == null) {
                    GeneralHelper.print("ERROR: You have to specify an IP!", GeneralHelper.TextColors.RED);
                    return;
                }
            }
        }

        if (!noGui) {
            gui = new ChatBotGui();
            GeneralHelper.initTextColors(getGui().getOutput().getStyledDocument());
        }

        int port = 25565;
        if (ip.contains(":")) {
            port = Integer.parseInt(ip.split(":")[1]);
            ip = ip.split(":")[0];
        }
        File loginFile = config.getLoginFile();
        if (!loginFile.exists()) {
            GeneralHelper.print("ERROR: No login file!", GeneralHelper.TextColors.RED);
            return;
        }

        String[] loginInfo = GeneralHelper.readFile(loginFile).split("\n");
        MinecraftAccount minecraftAccount;
        switch (config.getAccountType()) {
            case "MSA" -> minecraftAccount = new MinecraftAccount.MicrosoftAccount(loginInfo[0], loginInfo[1]);
            case "MOJ" -> minecraftAccount = loginInfo.length > 1 ? new MinecraftAccount.MojangAccount(loginInfo[0], loginInfo[1]) : new MinecraftAccount.MojangAccount(loginInfo[0]);
            default -> {
                GeneralHelper.print("ERROR: Unknown account type in config!", GeneralHelper.TextColors.RED);
                return;
            }
        }
        Session session = minecraftAccount.login();
        if (session == null) {
            GeneralHelper.print("ERROR: Login failed!", GeneralHelper.TextColors.RED);
            return;
        }
        GeneralHelper.print("Logged in. Starting connection to " + ip + ":" + port, GeneralHelper.TextColors.GREEN);

        connectionLoop(ip, port, session);

        GeneralHelper.print("Connection closed.", GeneralHelper.TextColors.RED);
    }

    private static void connectionLoop(String ip, int port, Session session) throws InterruptedException, IOException {
        try {
            clientConnection = new ClientConnection(ip, port, session);
            if (getGui() != null)
                getGui().setClientConnection(clientConnection);
            clientConnection.connect();
            timer.reset();
            while (clientConnection.isConnected()) {
                clientConnection.tick();
                if (getGui() != null) {
                    getGui().getFrame().setTitle("ChatBot - Connected for: " + GeneralHelper.getDurationString(timer.getPassed()));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (getConfig().isReconnect()) {
            GeneralHelper.print("Client disconnected, reconnecting in " + getConfig().getReconnectDelay() + " seconds...", GeneralHelper.TextColors.PURPLE);
            timer.reset();
            Thread.sleep(getConfig().getReconnectDelay() * 1000L);
            connectionLoop(ip, port, session);
        }
    }

    public static ChatBotGui getGui() {
        return gui;
    }

    public static Config getConfig() {
        return config;
    }

    public static ClientConnection getClientConnection() {
        return clientConnection;
    }
}
