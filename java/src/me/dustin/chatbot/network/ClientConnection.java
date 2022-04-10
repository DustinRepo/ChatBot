package me.dustin.chatbot.network;

import com.google.common.collect.Queues;
import com.google.gson.JsonObject;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import me.dustin.chatbot.ChatBot;
import me.dustin.chatbot.account.MinecraftAccount;
import me.dustin.chatbot.account.Session;
import me.dustin.chatbot.chat.ChatMessage;
import me.dustin.chatbot.chat.Translator;
import me.dustin.chatbot.command.CommandManager;
import me.dustin.chatbot.helper.GeneralHelper;
import me.dustin.chatbot.helper.TPSHelper;
import me.dustin.chatbot.network.packet.Packet;
import me.dustin.chatbot.network.packet.c2s.handshake.ServerBoundHandshakePacket;
import me.dustin.chatbot.network.packet.c2s.login.ServerBoundLoginStartPacket;
import me.dustin.chatbot.network.crypt.PacketCrypt;
import me.dustin.chatbot.network.packet.c2s.play.ServerBoundClientSettingsPacket;
import me.dustin.chatbot.network.packet.handler.ClientBoundLoginClientBoundPacketHandler;
import me.dustin.chatbot.network.packet.handler.ClientBoundPacketHandler;
import me.dustin.chatbot.network.packet.pipeline.PacketDecryptor;
import me.dustin.chatbot.network.packet.pipeline.PacketDeflater;
import me.dustin.chatbot.network.packet.pipeline.PacketEncryptor;
import me.dustin.chatbot.network.packet.pipeline.PacketInflater;
import me.dustin.chatbot.network.packet.s2c.play.ClientBoundJoinGamePacket;
import me.dustin.chatbot.network.player.ClientPlayer;
import me.dustin.chatbot.network.player.PlayerManager;
import me.dustin.chatbot.process.ProcessManager;
import me.dustin.chatbot.process.impl.*;
import me.dustin.events.EventManager;
import me.dustin.events.core.EventListener;
import me.dustin.events.core.annotate.EventPointer;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;

public class ClientConnection {

    private Channel channel;

    private final String ip;
    private final int port;

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
    private boolean isConnected;
    private boolean isEncrypted;
    private boolean isInGame;

    private final ClientPlayer clientPlayer;
    private final MinecraftAccount minecraftAccount;

    private final Queue<Packet> queuedPackets = Queues.newConcurrentLinkedQueue();

    public ClientConnection(String ip, int port, Session session, MinecraftAccount minecraftAccount) throws IOException {
        this.ip = ip;
        this.port = port;
        this.session = session;
        this.minecraftAccount = minecraftAccount;
        this.clientPlayer = new ClientPlayer(session.getUsername(), GeneralHelper.uuidFromStringNoDashes(session.getUuid()), this);
        this.clientBoundPacketHandler = new ClientBoundLoginClientBoundPacketHandler();
        this.commandManager = new CommandManager(this);
        this.processManager = new ProcessManager(this);
        this.packetCrypt = new PacketCrypt();
        this.tpsHelper = new TPSHelper();
        this.playerManager = new PlayerManager();
        this.eventManager = new EventManager();
        updateTranslations();
        getEventManager().register(this);
        getEventManager().register(ChatBot.getGui());
        isConnected = true;
    }

    @EventPointer
    private final EventListener<ClientBoundJoinGamePacket> eventClientBoundJoinGamePacketEventListener = new EventListener<>(event ->  {
        if (isInGame)
            return;
        isInGame = true;
        loadProcesses();
        sendPacket(new ServerBoundClientSettingsPacket(ChatBot.getConfig().getLocale(), ChatBot.getConfig().isAllowServerListing(), ServerBoundClientSettingsPacket.SkinPart.all()));
        GeneralHelper.print("Received Join Game. Sending ClientSettings packet and loading processes.", ChatMessage.TextColors.GOLD);
    });

    public void loadProcesses() {
        if (!getProcessManager().getProcesses().isEmpty())
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
        if (ChatBot.getConfig().isNumberCount())
            getProcessManager().addProcess(new NumberCountProcess(this));
    }

    public void connect() {
        this.commandManager.init();
        GeneralHelper.print("Setting client version to " + Protocols.getCurrent().getNames()[0] + " (" + Protocols.getCurrent().getProtocolVer() + ")", ChatMessage.TextColors.AQUA);
        GeneralHelper.print("Sending Handshake and LoginStart packets...", ChatMessage.TextColors.GREEN);
        sendPacket(new ServerBoundHandshakePacket(Protocols.getCurrent().getProtocolVer(), ip, port, ServerBoundHandshakePacket.LOGIN_STATE));
        sendPacket(new ServerBoundLoginStartPacket(getSession().getUsername()));
    }

    public boolean contactAuthServers(String serverHash) {
        JsonObject request = new JsonObject();
        request.addProperty("accessToken", getSession().getAccessToken());
        request.addProperty("selectedProfile", getSession().getUuid());
        request.addProperty("serverId", serverHash);
        Map<String, String> header = new HashMap<>();
        header.put("Content-Type", "application/json");

        GeneralHelper.HttpResponse resp = GeneralHelper.httpRequest("https://sessionserver.mojang.com/session/minecraft/join", request.toString(), header, "GET");
        if (resp.responseCode() != 204) {//Mojang decided our request to the auth servers wasn't good
            this.minecraftAccount.setLoginAgain(true);
            close();
            return false;
        }
        return true;
    }

    public void activateEncryption() {
        isEncrypted = true;
        this.channel.pipeline().addBefore("splitter", "decrypt", new PacketDecryptor());
        this.channel.pipeline().addBefore("size_prepender", "encrypt", new PacketEncryptor());
    }

    public void close() {
        if (!isConnected)
            return;
        getProcessManager().stopAll();
        if (channel != null)
            channel.close().awaitUninterruptibly();
        if (ChatBot.getGui() != null) {
            ChatBot.getGui().getPlayerList().clear();
        }
        isConnected = false;
        if (ChatBot.getConfig().isReconnect()) {
            GeneralHelper.print("Client disconnected, reconnecting in " + ChatBot.getConfig().getReconnectDelay() + " seconds...", ChatMessage.TextColors.DARK_PURPLE);
            try {
                Thread.sleep(ChatBot.getConfig().getReconnectDelay() * 1000L);
                ChatBot.createConnection(ip, port, session, minecraftAccount);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void updateTranslations() {
        Translator.setTranslation(ChatBot.getConfig().getLocale());
    }

    public void tick() {
        if (isInGame()) {
            getProcessManager().tick();
            getClientPlayer().tick();
        }
    }

    public void sendPacket(Packet packet) {
        if (channel != null && channel.isOpen()) {
            this.sendQueuedPackets();
            if (channel.eventLoop().inEventLoop()) {
                directSend(packet);
            } else {
                this.channel.eventLoop().execute(() -> directSend(packet));
            }
        } else {
            queuedPackets.add(packet);
        }
    }

    private void sendQueuedPackets() {
        if (this.channel != null && this.channel.isOpen()) {
            synchronized (this.queuedPackets) {
                Packet packet;
                while ((packet = queuedPackets.poll()) != null) {
                    directSend(packet);
                }
            }
        }
    }

    private void directSend(Packet packet) {
        ChannelFuture channelFuture = this.channel.writeAndFlush(packet);
        channelFuture.addListener((ChannelFutureListener) future -> {
            if (!future.isSuccess()) {
                future.channel().pipeline().fireExceptionCaught(future.cause());
            }
        });
    }

    public String getIp() {
        return ip;
    }

    public int getPort() {
        return port;
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

    public boolean isInGame() {
        return isInGame;
    }

    public void setCompressionThreshold(int compressionThreshold) {
        this.compressionThreshold = compressionThreshold;
        if (compressionThreshold >= 0) {
            if (this.channel.pipeline().get("decompress") instanceof PacketInflater) {
                ((PacketInflater)this.channel.pipeline().get("decompress")).setCompressionThreshold(compressionThreshold);
            } else {
                this.channel.pipeline().addBefore("decoder", "decompress", new PacketInflater(compressionThreshold));
            }

            if (this.channel.pipeline().get("compress") instanceof PacketDeflater) {
                ((PacketDeflater)this.channel.pipeline().get("compress")).setCompressionThreshold(compressionThreshold);
            } else {
                this.channel.pipeline().addBefore("encoder", "compress", new PacketDeflater(compressionThreshold));
            }
        } else {
            if (this.channel.pipeline().get("decompress") instanceof PacketInflater) {
                this.channel.pipeline().remove("decompress");
            }

            if (this.channel.pipeline().get("compress") instanceof PacketDeflater) {
                this.channel.pipeline().remove("compress");
            }
        }
    }

    public void setClientBoundPacketHandler(ClientBoundPacketHandler clientBoundPacketHandler) {
        this.clientBoundPacketHandler = clientBoundPacketHandler;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    public boolean isChannelOpen() {
        return channel != null && channel.isOpen();
    }

    public enum NetworkState {
        HANDSHAKE, PLAY, STATUS, LOGIN
    }
}
