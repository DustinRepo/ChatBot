package me.dustin.chatbot;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.proxy.Socks4ProxyHandler;
import io.netty.handler.proxy.Socks5ProxyHandler;
import io.netty.handler.timeout.ReadTimeoutHandler;
import me.dustin.chatbot.account.MinecraftAccount;
import me.dustin.chatbot.account.Session;
import me.dustin.chatbot.chat.ChatMessage;
import me.dustin.chatbot.config.Config;
import me.dustin.chatbot.gui.ChatBotGui;
import me.dustin.chatbot.helper.GeneralHelper;
import me.dustin.chatbot.helper.StopWatch;
import me.dustin.chatbot.network.ClientConnection;
import me.dustin.chatbot.network.packet.handler.ClientBoundPacketHandler;
import me.dustin.chatbot.network.packet.pipeline.*;
import me.dustin.chatbot.process.impl.QuoteProcess;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;

public class ChatBot {

    private static Config config;
    private static ClientConnection clientConnection;
    private static ChatBotGui gui;
    private static final StopWatch stopWatch = new StopWatch();

    public static void main(String[] args) throws IOException, InterruptedException {
        String jarPath = new File("").getAbsolutePath();
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
            try {
                if (System.getProperty("os.name").toLowerCase().contains("linux"))
                    UIManager.setLookAndFeel("com.sun.java.swing.plaf.gtk.GTKLookAndFeel");
                else
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                gui.updateComponents();
            } catch (Exception e) {}
        }

        config = new Config(new File(jarPath, "config.cfg"));

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

        createConnection(ip, port, session, minecraftAccount);
    }

    public static void createConnection(String ip, int port, Session session, MinecraftAccount minecraftAccount) throws InterruptedException {
        try {
            if (ChatBot.getConfig().isLog())
                GeneralHelper.initLogger();
            if (minecraftAccount.isLoginAgain()) {
                session = minecraftAccount.login();
                minecraftAccount.setLoginAgain(false);
            }
            boolean bl = false;
            if (clientConnection == null && getConfig().isQuotes())
                bl = true;
            clientConnection = new ClientConnection(ip, port, session, minecraftAccount);
            if (bl)
                QuoteProcess.readFile();
            Bootstrap bootstrap = new Bootstrap().group(new NioEventLoopGroup(0, (new ThreadFactoryBuilder()).setNameFormat("Netty Client IO #%d").setDaemon(true).build()));
            bootstrap = bootstrap.handler(new ChannelInitializer<>() {
                protected void initChannel(Channel channel) {
                    channel.config().setOption(ChannelOption.TCP_NODELAY, true);
                    String proxyString = getConfig().getProxyString();
                    if (!proxyString.isEmpty()) {
                        String proxyIP = proxyString.split(":")[0];
                        int proxyPort = Integer.parseInt(proxyString.split(":")[1]);
                        if (getConfig().getProxySOCKS() == 5)
                            channel.pipeline().addFirst(new Socks5ProxyHandler(new InetSocketAddress(proxyIP, proxyPort), getConfig().getProxyUsername(), getConfig().getProxyPassword()));
                        else
                            channel.pipeline().addFirst(new Socks4ProxyHandler(new InetSocketAddress(proxyIP, proxyPort)));
                    }

                    channel.pipeline().addLast("timeout", new ReadTimeoutHandler(30));
                    channel.pipeline().addLast("splitter", new PacketSplitterHandler());
                    channel.pipeline().addLast("decoder", new PacketDecoderHandler());
                    channel.pipeline().addLast("size_prepender", new PacketSizePrepender());
                    channel.pipeline().addLast("encoder", new PacketEncoder());
                    channel.pipeline().addLast("packet_handler", new ClientBoundPacketHandler());
                    channel.pipeline().addLast(new NetworkExceptionHandler());
                }
            });
            bootstrap = bootstrap.channel(NioSocketChannel.class);
            try {
                bootstrap.connect(ip, port).syncUninterruptibly();
                clientConnection.connect();
                stopWatch.reset();
            } catch (Exception e) {
                GeneralHelper.print(e.getMessage(), ChatMessage.TextColors.DARK_RED);
                clientConnection.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
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
