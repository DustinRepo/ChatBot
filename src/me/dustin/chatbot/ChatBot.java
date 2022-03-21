package me.dustin.chatbot;

import me.dustin.chatbot.account.MinecraftAccount;
import me.dustin.chatbot.account.Session;
import me.dustin.chatbot.chat.ChatMessage;
import me.dustin.chatbot.config.Config;
import me.dustin.chatbot.gui.ChatBotGui;
import me.dustin.chatbot.helper.GeneralHelper;
import me.dustin.chatbot.helper.StopWatch;
import me.dustin.chatbot.network.ClientConnection;

import javax.swing.*;
import java.io.File;
import java.io.IOException;

public class ChatBot {

    private static Config config;
    private static ClientConnection clientConnection;
    private static ChatBotGui gui;
    private static final StopWatch stopWatch = new StopWatch();

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

        if (!noGui) {
            gui = new ChatBotGui();
            for (UIManager.LookAndFeelInfo installedLookAndFeel : UIManager.getInstalledLookAndFeels()) {
                System.out.println(installedLookAndFeel.getClassName());
            }
            try {
                if (System.getProperty("os.name").toLowerCase().contains("win"))
                    UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
                else
                    UIManager.setLookAndFeel("com.sun.java.swing.plaf.gtk.GTKLookAndFeel");
                gui.updateComponents();
            } catch (Exception e) {}
        }

        if (ip == null) {
            if (noGui) {
                GeneralHelper.print("ERROR: No IP specified in arguments! Use --ip=<ip:port>!", ChatMessage.TextColors.RED);
                return;
            } else {
                ip = JOptionPane.showInputDialog("Input ip or ip:port");
                if (ip == null) {
                    GeneralHelper.print("ERROR: You have to specify an IP!", ChatMessage.TextColors.RED);
                    return;
                }
            }
        }

        int port = 25565;
        if (ip.contains(":")) {
            port = Integer.parseInt(ip.split(":")[1]);
            ip = ip.split(":")[0];
        }
        File loginFile = config.getLoginFile();
        if (!loginFile.exists()) {
            GeneralHelper.print("ERROR: No login file!", ChatMessage.TextColors.RED);
            return;
        }

        String[] loginInfo = GeneralHelper.readFile(loginFile).split("\n");
        MinecraftAccount minecraftAccount;
        switch (config.getAccountType()) {
            case "MSA" -> minecraftAccount = new MinecraftAccount.MicrosoftAccount(loginInfo[0], loginInfo[1]);
            case "MOJ" -> minecraftAccount = loginInfo.length > 1 ? new MinecraftAccount.MojangAccount(loginInfo[0], loginInfo[1]) : new MinecraftAccount.MojangAccount(loginInfo[0]);
            default -> {
                GeneralHelper.print("ERROR: Unknown account type in config!", ChatMessage.TextColors.RED);
                return;
            }
        }
        Session session = minecraftAccount.login();
        if (session == null) {
            GeneralHelper.print("ERROR: Login failed!", ChatMessage.TextColors.RED);
            return;
        }
        GeneralHelper.print("Logged in. Starting connection to " + ip + ":" + port, ChatMessage.TextColors.AQUA);

        connectionLoop(ip, port, session);

        if (clientConnection != null)
            clientConnection.getProcessManager().stopAll();
        GeneralHelper.print("Connection closed.", ChatMessage.TextColors.RED);
    }

    private static void connectionLoop(String ip, int port, Session session) throws InterruptedException {
        try {
            if (ChatBot.getConfig().isLog())
                GeneralHelper.initLogger();
            if (clientConnection != null)
                clientConnection.getProcessManager().stopAll();

            clientConnection = new ClientConnection(ip, port, session);
            if (getGui() != null)
                getGui().setClientConnection(clientConnection);
            clientConnection.connect();
            stopWatch.reset();
            while (clientConnection.isConnected()) {
                clientConnection.tick();
                if (getGui() != null) {
                    getGui().tick();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (getConfig().isReconnect()) {
            GeneralHelper.print("Client disconnected, reconnecting in " + getConfig().getReconnectDelay() + " seconds...", ChatMessage.TextColors.DARK_PURPLE);
            stopWatch.reset();
            Thread.sleep(getConfig().getReconnectDelay() * 1000L);
            connectionLoop(ip, port, session);
        }
    }

    public static long connectionTime() {
        return stopWatch.getPassed();
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
