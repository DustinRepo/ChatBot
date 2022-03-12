package me.dustin.chatbot.network;

import com.google.gson.JsonObject;
import me.dustin.chatbot.ChatBot;
import me.dustin.chatbot.account.Session;
import me.dustin.chatbot.command.CommandManager;
import me.dustin.chatbot.helper.GeneralHelper;
import me.dustin.chatbot.helper.TPSHelper;
import me.dustin.chatbot.network.packet.Packet;
import me.dustin.chatbot.network.packet.c2s.login.ServerBoundHandshakePacket;
import me.dustin.chatbot.network.packet.c2s.login.ServerBoundLoginStartPacket;
import me.dustin.chatbot.network.crypt.PacketCrypt;
import me.dustin.chatbot.network.packet.c2s.play.ServerBoundChatPacket;
import me.dustin.chatbot.network.packet.handler.ClientBoundLoginClientBoundPacketHandler;
import me.dustin.chatbot.network.packet.handler.ClientBoundPacketHandler;
import me.dustin.chatbot.network.player.ClientPlayer;
import me.dustin.chatbot.network.player.PlayerManager;

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
import java.util.Random;
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

    private ClientBoundPacketHandler clientBoundPacketHandler;
    private NetworkState networkState = NetworkState.LOGIN;
    private int compressionThreshold;
    private boolean isEncrypted;
    private boolean isConnected;

    private final ClientPlayer clientPlayer;

    private long lastAnnouncement = -1;
    private final String[] announcements = new String[]{"Use {PREFIX}help to get a list of my commands", "Use {PREFIX}coinflip to flip a coin", "Use {PREFIX}worstping or {PREFIX}bestping to see who has the lowest/highest ping", "Use {PREFIX}coffee to get a picture of coffee", "Need to report someone? Use {PREFIX}report <name> <reason>", "Use {PREFIX}isEven to see if a number is even!", "Need to see server TPS? {PREFIX}tps", "Want to use this bot program? https://github.com/DustinRepo/ChatBot"};

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
        this.clientPlayer = new ClientPlayer(session.getUsername(), UUID.fromString(session.getUuid().replaceFirst(
                "(\\p{XDigit}{8})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}+)", "$1-$2-$3-$4-$5"
        )), this);
        this.clientBoundPacketHandler = new ClientBoundLoginClientBoundPacketHandler(this);
        this.commandManager = new CommandManager(this);
        this.packetCrypt = new PacketCrypt();
        this.tpsHelper = new TPSHelper();
        this.playerManager = new PlayerManager();
    }

    public void connect() {
        this.isConnected = true;
        this.lastAnnouncement = System.currentTimeMillis();
        this.commandManager.init();
        this.getClientPlayer().updateKeepAlive();
        GeneralHelper.print("Sending Handshake and LoginStart packets...", GeneralHelper.ANSI_GREEN);
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
            socket.close();
            isConnected = false;
            if (ChatBot.getGui() != null) {
                ChatBot.getGui().getPlayerList().clear();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void tick() {
        getClientPlayer().tick();
        if (ChatBot.getConfig().getAnnouncementDelay() > 0) {
            if (System.currentTimeMillis() - lastAnnouncement >= ChatBot.getConfig().getAnnouncementDelay() * 1000L && getNetworkState() == NetworkState.PLAY) {
                int size = announcements.length;
                Random random = new Random();
                int select = random.nextInt(size);
                sendPacket(new ServerBoundChatPacket((ChatBot.getConfig().isGreenText() ? ">" : "") + announcements[select].replace("{PREFIX}", ChatBot.getConfig().getCommandPrefix())));
                lastAnnouncement = System.currentTimeMillis();
            }
        }
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

                    //TODO: test this to see if it actually works.
                    //it seems unnecessary currently, doesn't seem most packets go over the threshold
                    /*if (size > this.getCompressionThreshold()) {
                        GeneralHelper.print("Compressing packet", GeneralHelper.ANSI_PURPLE);
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

    public PacketCrypt getPacketCrypt() {
        return packetCrypt;
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
