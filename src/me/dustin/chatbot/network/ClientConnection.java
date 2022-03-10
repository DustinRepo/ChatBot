package me.dustin.chatbot.network;

import com.google.gson.JsonObject;
import me.dustin.chatbot.ChatBot;
import me.dustin.chatbot.account.Session;
import me.dustin.chatbot.command.CommandManager;
import me.dustin.chatbot.helper.GeneralHelper;
import me.dustin.chatbot.helper.TPSHelper;
import me.dustin.chatbot.network.packet.ClientBoundPacketListener;
import me.dustin.chatbot.network.packet.Packet;
import me.dustin.chatbot.network.packet.c2s.login.ServerBoundHandshakePacket;
import me.dustin.chatbot.network.packet.c2s.login.ServerBoundLoginStartPacket;
import me.dustin.chatbot.network.crypt.PacketCrypt;
import me.dustin.chatbot.network.packet.c2s.play.ServerBoundChatPacket;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class ClientConnection {

    private final Socket socket;
    private final String ip;
    private final int port;
    private DataInputStream in;
    private DataOutputStream out;

    private final Session session;
    private final ClientBoundPacketHandler clientBoundPacketHandler;
    private final ClientBoundPacketListener clientBoundPacketListener;
    private final CommandManager commandManager;
    private final PacketCrypt packetCrypt;
    private final TPSHelper tpsHelper;

    private NetworkState networkState = NetworkState.LOGIN;
    private int compressionThreshold;
    private boolean isEncrypted;
    private boolean isConnected;

    private long lastAnnouncement = -1;
    private final String[] announcements = new String[]{"Use !help to get a list of my commands", "Need to report someone? Use {PREFIX}report <name> <reason>", "Use {PREFIX}isEven to see if a number is even!", "Need to see server TPS? {PREFIX}tps"};

    public ClientConnection(String ip, int port, Session session) throws IOException {
        this.ip = ip;
        this.port = port;
        this.socket = new Socket(ip, port);
        this.in = new DataInputStream(socket.getInputStream());
        this.out = new DataOutputStream(socket.getOutputStream());
        this.session = session;
        this.clientBoundPacketHandler = new ClientBoundPacketHandler(this);
        this.clientBoundPacketListener = new ClientBoundPacketListener(this);
        this.commandManager = new CommandManager(this);
        this.packetCrypt = new PacketCrypt();

        this.tpsHelper = new TPSHelper();
    }

    public void connect() {
        this.isConnected = true;
        this.commandManager.init();
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
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void tick() {
        getClientBoundPacketListener().listen();
        if (System.currentTimeMillis() - lastAnnouncement >= ChatBot.getConfig().getAnnouncementDelay() * 1000L && getNetworkState() == NetworkState.PLAY) {
            int size = announcements.length - 1;
            Random random = new Random();
            int select = random.nextInt(size);
            sendPacket(new ServerBoundChatPacket((ChatBot.getConfig().isGreenText() ? ">" : "") + announcements[select].replace("{PREFIX}", ChatBot.getConfig().getCommandPrefix())));
            lastAnnouncement = System.currentTimeMillis();
        }
    }

    public void sendPacket(Packet packet) {
        try {
            byte[] data = packet.createPacket().toByteArray();
            if (data.length > 0) {
                if (this.compressionThreshold > 0) {
                    //deconstruct the packet to rebuild back into compressed format
                    ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(data);
                    int size = Packet.readVarInt(byteArrayInputStream);
                    byte[] packetData = new byte[size];
                    byteArrayInputStream.read(packetData, 0, size);

                    ByteArrayInputStream packetDataInputStream = new ByteArrayInputStream(packetData);
                    DataInputStream packetDataStream = new DataInputStream(packetDataInputStream);

                    int packetId = Packet.readVarInt(packetDataStream);

                    Packet.writeVarInt(out, packetData.length + Packet.sizeOfVarInt(packetId));//write size of packet id + data
                    Packet.writeVarInt(out, 0);//send 0 so the server knows it's not compressed
                    out.write(packetData);
                    out.flush();
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

    public ClientBoundPacketHandler getClientBoundPacketHandler() {
        return clientBoundPacketHandler;
    }

    public ClientBoundPacketListener getClientBoundPacketListener() {
        return clientBoundPacketListener;
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

    public enum NetworkState {
        HANDSHAKE, PLAY, STATUS, LOGIN
    }
}
