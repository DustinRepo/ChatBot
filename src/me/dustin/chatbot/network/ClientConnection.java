package me.dustin.chatbot.network;

import com.google.gson.JsonObject;
import me.dustin.chatbot.ChatBot;
import me.dustin.chatbot.account.Session;
import me.dustin.chatbot.chat.ChatMessage;
import me.dustin.chatbot.chat.Translator;
import me.dustin.chatbot.command.CommandManager;
import me.dustin.chatbot.config.Config;
import me.dustin.chatbot.event.EventLoginSuccess;
import me.dustin.chatbot.helper.GeneralHelper;
import me.dustin.chatbot.helper.TPSHelper;
import me.dustin.chatbot.network.packet.Packet;
import me.dustin.chatbot.network.packet.c2s.login.ServerBoundHandshakePacket;
import me.dustin.chatbot.network.packet.c2s.login.ServerBoundLoginStartPacket;
import me.dustin.chatbot.network.crypt.PacketCrypt;
import me.dustin.chatbot.network.packet.c2s.play.ServerBoundClientSettingsPacket;
import me.dustin.chatbot.network.packet.handler.ClientBoundLoginClientBoundPacketHandler;
import me.dustin.chatbot.network.packet.handler.ClientBoundPacketHandler;
import me.dustin.chatbot.network.player.ClientPlayer;
import me.dustin.chatbot.network.player.PlayerManager;
import me.dustin.chatbot.process.ProcessManager;
import me.dustin.chatbot.process.impl.*;
import me.dustin.events.EventManager;
import me.dustin.events.core.EventListener;
import me.dustin.events.core.annotate.EventPointer;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Authenticator;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ClientConnection {

    private final Socket socket;
    private final String ip;
    private final int port;
    private DataInputStream in;
    private DataOutputStream out;

    private final Session session;
    private final PlayerManager playerManager;
    private final CommandManager commandManager;
    private final PacketCrypt packetCrypt;
    private final TPSHelper tpsHelper;
    private final EventManager eventManager;
    private final ProcessManager processManager;

    private ClientBoundPacketHandler clientBoundPacketHandler;
    private NetworkState networkState = NetworkState.LOGIN;
    private int compressionThreshold;
    private boolean isEncrypted;
    private boolean isConnected;

    private final ClientPlayer clientPlayer;

    public ClientConnection(String ip, int port, Session session) throws IOException {
        this.ip = ip;
        this.port = port;
        String proxyString = ChatBot.getConfig().getProxyString();
        if (!proxyString.isEmpty()) {
            String proxyIP = proxyString.split(":")[0];
            int proxyPort = Integer.parseInt(proxyString.split(":")[1]);
            Proxy proxy = new Proxy(Proxy.Type.SOCKS, new InetSocketAddress(proxyIP, proxyPort));
            if (!ChatBot.getConfig().getProxyUsername().isEmpty()) {
                Authenticator.setDefault(GeneralHelper.getAuth(ChatBot.getConfig().getProxyUsername(), ChatBot.getConfig().getProxyPassword()));
            }
            this.socket = new Socket(proxy);
            this.socket.connect(new InetSocketAddress(ip, port), 10000);
        }else
            this.socket = new Socket(ip, port);
        this.in = new DataInputStream(socket.getInputStream());
        this.out = new DataOutputStream(socket.getOutputStream());
        this.session = session;
        this.clientPlayer = new ClientPlayer(session.getUsername(), GeneralHelper.uuidFromStringNoDashes(session.getUuid()), this);
        this.clientBoundPacketHandler = new ClientBoundLoginClientBoundPacketHandler(this);
        this.commandManager = new CommandManager(this);
        this.processManager = new ProcessManager(this);
        this.packetCrypt = new PacketCrypt();
        this.tpsHelper = new TPSHelper();
        this.playerManager = new PlayerManager();
        this.eventManager = new EventManager();

        updateTranslations();
        getEventManager().register(this);
        getEventManager().register(ChatBot.getGui());
    }

    @EventPointer
    private final EventListener<EventLoginSuccess> eventLoginSuccessEventListener = new EventListener<>(event -> {
        loadProcesses();
        if (!ChatBot.getConfig().isSkinBlink())
            new Thread(() -> {
                try {
                    //wait 5 seconds so vanilla servers don't throw a shit-fit
                    Thread.sleep(5000);
                    sendPacket(new ServerBoundClientSettingsPacket(ChatBot.getConfig().getLocale(), ChatBot.getConfig().isAllowServerListing(), ServerBoundClientSettingsPacket.SkinPart.all()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
    });

    public void loadProcesses() {
        getProcessManager().stopAll();
        if (ChatBot.getConfig().isAntiAFK())
            getProcessManager().addProcess(new AntiAFKProcess(this));
        if (ChatBot.getConfig().isCrackedLogin())
            getProcessManager().addProcess(new CrackedLoginProcess(this));
        if (ChatBot.getConfig().isAnnouncements())
            getProcessManager().addProcess(new AnnouncementProcess(this));
        if (ChatBot.getConfig().isSkinBlink())
            getProcessManager().addProcess(new SkinBlinkProcess(this));
        if (ChatBot.getConfig().is2b2tCheck())
            getProcessManager().addProcess(new Chat2b2tProcess(this));
        if (ChatBot.getConfig().is2b2tCount())
            getProcessManager().addProcess(new Chat2b2tCountProcess(this));
        if (ChatBot.getConfig().isQuotes())
            getProcessManager().addProcess(new QuoteProcess(this));
    }

    public void connect() {
        this.isConnected = true;
        this.commandManager.init();
        GeneralHelper.print("Setting client version to " + ChatBot.getConfig().getClientVersion() + " (" + ChatBot.getConfig().getProtocolVersion() + ")", ChatMessage.TextColors.AQUA);
        GeneralHelper.print("Sending Handshake and LoginStart packets...", ChatMessage.TextColors.GREEN);
        sendPacket(new ServerBoundHandshakePacket(ChatBot.getConfig().getProtocolVersion(), ip, port, ServerBoundHandshakePacket.LOGIN_STATE));
        sendPacket(new ServerBoundLoginStartPacket(getSession().getUsername()));
    }

    public void contactAuthServers(String serverHash) {
        JsonObject request = new JsonObject();
        request.addProperty("accessToken", getSession().getAccessToken());
        request.addProperty("selectedProfile", getSession().getUuid());
        request.addProperty("serverId", serverHash);
        Map<String, String> header = new HashMap<>();
        header.put("Content-Type", "application/json");

        GeneralHelper.httpRequest("https://sessionserver.mojang.com/session/minecraft/join", request.toString(), header, "GET");
    }

    public void activateEncryption() {
        try {
            isEncrypted = true;
            this.out.flush();
            this.out = new DataOutputStream(getPacketCrypt().encryptOutputStream(this.socket.getOutputStream()));
            this.in = new DataInputStream(getPacketCrypt().decryptInputStream(this.socket.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void close() {
        try {
            getProcessManager().stopAll();
            socket.close();
            isConnected = false;
            if (ChatBot.getGui() != null) {
                ChatBot.getGui().getPlayerList().clear();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void updateTranslations() {
        Translator.setTranslation(ChatBot.getConfig().getLocale());
    }

    public void tick() {
        getProcessManager().tick();
        getClientPlayer().tick();
        getClientBoundPacketHandler().listen();
    }

    public void sendPacket(Packet packet) {
        if (!isConnected())
            return;
        try {
            byte[] data = packet.createPacket().toByteArray();
            if (data.length > 0) {
                if (this.getCompressionThreshold() > 0) {
                    //deconstruct the packet to rebuild back into compressed format
                    ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(data);
                    DataInputStream first = new DataInputStream(byteArrayInputStream);
                    int size = Packet.readVarInt(byteArrayInputStream);
                    byte[] packetData = new byte[size];
                    first.readFully(packetData, 0, size);

                    //TODO: test this to see if it actually works. it seems unnecessary currently, doesn't seem most packets go over the threshold
                    /*if (size > this.getCompressionThreshold()) {
                        GeneralHelper.print("Compressing packet", ChatMessage.TextColors.PURPLE);
                        byte[] compressed = new byte[1024];

                        Deflater deflater = new Deflater();
                        deflater.setInput(packetData);
                        int compressedSize = deflater.deflate(compressed);

                        Packet.writeVarInt(out, size + compressedSize);//Length of Data Length + compressed length of (Packet ID + Data)
                        Packet.writeVarInt(out, size);//Length of uncompressed (Packet ID + Data)
                        out.write(compressed);
                    } else {*/
                        ByteArrayInputStream packetDataInputStream = new ByteArrayInputStream(packetData);
                        DataInputStream second = new DataInputStream(packetDataInputStream);

                        int packetId = Packet.readVarInt(second);

                        Packet.writeVarInt(out, packetData.length + Packet.sizeOfVarInt(packetId));//write size of packet id + data
                        Packet.writeVarInt(out, 0);//send 0 so the server knows it's not compressed
                        out.write(packetData);
                        out.flush();
                    //}
                } else {
                    out.write(data);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getIp() {
        return ip;
    }

    public int getPort() {
        return port;
    }

    public DataInputStream getIn() {
        return in;
    }

    public DataOutputStream getOut() {
        return out;
    }

    public Session getSession() {
        return session;
    }

    public ClientPlayer getClientPlayer() {
        return clientPlayer;
    }

    public ClientBoundPacketHandler getClientBoundPacketHandler() {
        return clientBoundPacketHandler;
    }

    public TPSHelper getTpsHelper() {
        return tpsHelper;
    }

    public CommandManager getCommandManager() {
        return commandManager;
    }

    public ProcessManager getProcessManager() {
        return processManager;
    }

    public PacketCrypt getPacketCrypt() {
        return packetCrypt;
    }

    public EventManager getEventManager() {
        return eventManager;
    }

    public Socket getSocket() {
        return socket;
    }

    public NetworkState getNetworkState() {
        return networkState;
    }

    public PlayerManager getPlayerManager() {
        return playerManager;
    }

    public void setNetworkState(NetworkState networkState) {
        this.networkState = networkState;
    }

    public int getCompressionThreshold() {
        return compressionThreshold;
    }

    public boolean isEncrypted() {
        return isEncrypted;
    }

    public boolean isConnected() {
        return isConnected;
    }

    public void setConnected(boolean connected) {
        isConnected = connected;
    }

    public void setCompressionThreshold(int compressionThreshold) {
        this.compressionThreshold = compressionThreshold;
    }

    public void setClientBoundPacketHandler(ClientBoundPacketHandler clientBoundPacketHandler) {
        this.clientBoundPacketHandler = clientBoundPacketHandler;
    }

    public enum NetworkState {
        HANDSHAKE, PLAY, STATUS, LOGIN
    }
}
