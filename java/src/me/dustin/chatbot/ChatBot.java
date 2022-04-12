package me.dustin.chatbot;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.google.gson.JsonObject;
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
import me.dustin.chatbot.network.MinecraftServerAddress;
import me.dustin.chatbot.network.packet.Packet;
import me.dustin.chatbot.network.packet.ProtocolHandler;
import me.dustin.chatbot.network.packet.c2s.handshake.ServerBoundHandshakePacket;
import me.dustin.chatbot.network.packet.c2s.query.ServerBoundPingPacket;
import me.dustin.chatbot.network.packet.c2s.query.ServerBoundQueryRequestPacket;
import me.dustin.chatbot.network.packet.handler.ClientBoundPacketHandler;
import me.dustin.chatbot.network.packet.pipeline.*;
import me.dustin.chatbot.network.packet.s2c.query.ClientBoundQueryResponsePacket;
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
        File f = new File(System.getProperty("java.class.path"));
        File dir = f.getAbsoluteFile().getParentFile();
        String jarPath = dir.toString();
        if (jarPath.contains(":"))
            jarPath = new File("").getAbsolutePath();
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
            } catch (Exception e) {}
            gui.updateComponents();
        }

        config = new Config(new File(jarPath, "config.cfg"));
        GeneralHelper.print("Downloading protocol data...", ChatMessage.TextColor.YELLOW);
        ProtocolHandler.downloadData();

        if (ip == null) {
            if (noGui) {
                GeneralHelper.print("ERROR: No IP specified in arguments! Use --ip=<ip:port>!", ChatMessage.TextColor.RED);
                return;
            } else {
                ip = JOptionPane.showInputDialog("Input ip or ip:port");
                if (ip == null) {
                    GeneralHelper.print("ERROR: You have to specify an IP!", ChatMessage.TextColor.RED);
                    return;
                }
            }
        }

        int port = 25565;
        if (ip.contains(":")) {
            port = Integer.parseInt(ip.split(":")[1]);
            ip = ip.split(":")[0];
        }

        MinecraftServerAddress minecraftServerAddress = MinecraftServerAddress.resolve(ip, port);
        if (minecraftServerAddress == null) {
            GeneralHelper.print("Could not resolve hostname " + ip + "!", ChatMessage.TextColor.RED);
            return;
        }

        File loginFile = config.getLoginFile();
        if (!loginFile.exists()) {
            GeneralHelper.print("ERROR: No login file!", ChatMessage.TextColor.RED);
            return;
        }
        if (getConfig().getClientVersion().equalsIgnoreCase("auto"))
            pingServer(minecraftServerAddress);

        String[] loginInfo = GeneralHelper.readFile(loginFile).split("\n");
        MinecraftAccount minecraftAccount;
        switch (config.getAccountType()) {
            case "MSA" -> minecraftAccount = new MinecraftAccount.MicrosoftAccount(loginInfo[0], loginInfo[1]);
            case "MOJ" -> minecraftAccount = loginInfo.length > 1 ? new MinecraftAccount.MojangAccount(loginInfo[0], loginInfo[1]) : new MinecraftAccount.MojangAccount(loginInfo[0]);
            default -> {
                GeneralHelper.print("ERROR: Unknown account type in config!", ChatMessage.TextColor.RED);
                return;
            }
        }
        Session session = minecraftAccount.login();
        if (session == null) {
            GeneralHelper.print("ERROR: Login failed!", ChatMessage.TextColor.RED);
            return;
        }
        GeneralHelper.print("Starting connection to " + ip + ":" + port, ChatMessage.TextColor.AQUA);

        createConnection(minecraftServerAddress, session, minecraftAccount);
    }

    public static void createConnection(MinecraftServerAddress minecraftServerAddress, Session session, MinecraftAccount minecraftAccount) throws InterruptedException {
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
            clientConnection = new ClientConnection(minecraftServerAddress, session, minecraftAccount);
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
                bootstrap.connect(minecraftServerAddress.getIp(), minecraftServerAddress.getPort()).sync();
                clientConnection.connect();
                stopWatch.reset();
            } catch (Exception e) {
                GeneralHelper.print(e.getMessage(), ChatMessage.TextColor.DARK_RED);
                clientConnection.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void pingServer(MinecraftServerAddress minecraftServerAddress) {
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
                channel.pipeline().addLast("decoder", new QueryPacketDecoderHandler());
                channel.pipeline().addLast("size_prepender", new PacketSizePrepender());
                channel.pipeline().addLast("encoder", new PacketEncoder());
                channel.pipeline().addLast("packet_handler", new SimpleChannelInboundHandler<Packet.ClientBoundPacket>() {
                    @Override
                    protected void channelRead0(ChannelHandlerContext ctx, Packet.ClientBoundPacket msg) throws Exception {
                        ClientBoundQueryResponsePacket responsePacket = (ClientBoundQueryResponsePacket)msg;
                        JsonObject jsonObject = GeneralHelper.gson.fromJson(responsePacket.getJsonData(), JsonObject.class);
                        JsonObject version = jsonObject.getAsJsonObject("version");
                        getConfig().setProtocolVersion(version.get("protocol").getAsInt());
                        ProtocolHandler.setCurrent(getConfig().getProtocolVersion());
                        getConfig().setClientVersion(ProtocolHandler.getCurrent().getName());
                        String s = ChatMessage.parse(jsonObject.get("description"));
                        GeneralHelper.print("Server MOTD:", ChatMessage.TextColor.YELLOW);
                        for (String s1 : s.split("\n")) {
                            GeneralHelper.printColorText(s1);
                        }
                    }
                });
                channel.pipeline().addLast(new NetworkExceptionHandler());
            }
        });
        bootstrap = bootstrap.channel(NioSocketChannel.class);
        ChannelFuture channelFuture = bootstrap.connect(minecraftServerAddress.getIp(), minecraftServerAddress.getPort());
        channelFuture.addListener(future -> {
            if (future.isSuccess()) {
                channelFuture.channel().writeAndFlush(new ServerBoundHandshakePacket(ProtocolHandler.getCurrent().getProtocolVer(), minecraftServerAddress.getIp(), minecraftServerAddress.getPort(), ServerBoundHandshakePacket.STATUS_STATE));
                channelFuture.channel().writeAndFlush(new ServerBoundQueryRequestPacket());
                channelFuture.channel().writeAndFlush(new ServerBoundPingPacket(System.currentTimeMillis()));
            }
        });
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
