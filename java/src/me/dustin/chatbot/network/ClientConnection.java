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
import me.dustin.chatbot.event.EventTick;
import me.dustin.chatbot.helper.GeneralHelper;
import me.dustin.chatbot.helper.KeyHelper;
import me.dustin.chatbot.helper.StopWatch;
import me.dustin.chatbot.helper.TPSHelper;
import me.dustin.chatbot.network.key.KeyContainer;
import me.dustin.chatbot.network.key.SaltAndSig;
import me.dustin.chatbot.network.packet.Packet;
import me.dustin.chatbot.network.packet.impl.handshake.ServerBoundHandshakePacket;
import me.dustin.chatbot.network.packet.impl.login.c2s.ServerBoundLoginStartPacket;
import me.dustin.chatbot.network.crypt.PacketCrypt;
import me.dustin.chatbot.network.packet.handler.LoginClientBoundPacketHandler;
import me.dustin.chatbot.network.packet.handler.ClientBoundPacketHandler;
import me.dustin.chatbot.network.packet.impl.play.c2s.ServerBoundChatPacket;
import me.dustin.chatbot.network.packet.impl.play.c2s.ServerBoundCommandPacket;
import me.dustin.chatbot.network.packet.pipeline.PacketDecryptor;
import me.dustin.chatbot.network.packet.pipeline.PacketDeflater;
import me.dustin.chatbot.network.packet.pipeline.PacketEncryptor;
import me.dustin.chatbot.network.packet.pipeline.PacketInflater;
import me.dustin.chatbot.network.packet.impl.play.s2c.ClientBoundJoinGamePacket;
import me.dustin.chatbot.entity.player.ClientPlayer;
import me.dustin.chatbot.entity.player.PlayerInfoManager;
import me.dustin.chatbot.world.World;
import me.dustin.chatbot.process.ProcessManager;
import me.dustin.chatbot.process.impl.*;
import me.dustin.events.EventManager;
import me.dustin.events.core.EventListener;
import me.dustin.events.core.annotate.EventPointer;

import java.io.IOException;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;

public class ClientConnection {

    private Channel channel;

    private final MinecraftServerAddress minecraftServerAddress;

    private final Session session;
    private final PlayerInfoManager playerInfoManager;
    private final CommandManager commandManager;
    private final PacketCrypt packetCrypt;
    private final TPSHelper tpsHelper;
    private final EventManager eventManager;
    private final ProcessManager processManager;

    private ClientBoundPacketHandler clientBoundPacketHandler;
    private NetworkState networkState = NetworkState.LOGIN;
    private final KeyContainer keyContainer;
    private int compressionThreshold;
    private boolean isConnected;
    private boolean isEncrypted;
    private boolean isInGame;

    private final World world;
    private final ClientPlayer clientPlayer;
    private final MinecraftAccount minecraftAccount;

    private final Queue<Packet> queuedPackets = Queues.newConcurrentLinkedQueue();

    private final StopWatch tickWatch = new StopWatch();

    public ClientConnection(MinecraftServerAddress minecraftServerAddress, Session session, MinecraftAccount minecraftAccount) throws IOException {
        this.minecraftServerAddress = minecraftServerAddress;
        this.session = session;
        this.minecraftAccount = minecraftAccount;
        this.clientPlayer = new ClientPlayer(session.getUsername(), GeneralHelper.uuidFromStringNoDashes(session.getUuid()), this);
        this.world = new World(this);
        this.clientBoundPacketHandler = new LoginClientBoundPacketHandler();
        this.commandManager = new CommandManager(this);
        this.processManager = new ProcessManager(this);
        this.packetCrypt = new PacketCrypt();
        this.tpsHelper = new TPSHelper();
        this.playerInfoManager = new PlayerInfoManager();
        this.eventManager = new EventManager();
        if (minecraftAccount instanceof MinecraftAccount.MicrosoftAccount || minecraftAccount instanceof MinecraftAccount.MojangAccount mojangAccount && !mojangAccount.isCracked())
            this.keyContainer = KeyHelper.getKeyContainer(KeyHelper.getKeyPairResponse(session.getAccessToken()));
        else
            this.keyContainer = null;
        updateTranslations();
        getEventManager().register(this);
        if (ChatBot.getGui() != null)
            getEventManager().register(ChatBot.getGui());
        isConnected = true;
        new Thread(this::tick).start();
    }

    @EventPointer
    private final EventListener<ClientBoundJoinGamePacket> eventClientBoundJoinGamePacketEventListener = new EventListener<>(event ->  {
        if (isInGame)
            return;
        isInGame = true;
        loadProcesses();
    });

    public void loadProcesses() {
        if (!getProcessManager().getProcesses().isEmpty())
            getProcessManager().stopAll();
        getProcessManager().getProcesses().clear();
        if (ChatBot.getConfig().isAntiAFK())
            getProcessManager().addProcess(new AntiAFKProcess(this));
        if (ChatBot.getConfig().isCrackedLogin())
            getProcessManager().addProcess(new CrackedLoginProcess(this));
        if (ChatBot.getConfig().isAnnouncements())
            getProcessManager().addProcess(new AnnouncementProcess(this));
        if (ChatBot.getConfig().isSkinBlink() && ProtocolHandler.getCurrent().getProtocolVer() > ProtocolHandler.getVersionFromName("1.8.9").getProtocolVer())
            getProcessManager().addProcess(new SkinBlinkProcess(this));
        if (ChatBot.getConfig().is2b2tCheck())
            getProcessManager().addProcess(new Chat2b2tProcess(this));
        if (ChatBot.getConfig().is2b2tCount())
            getProcessManager().addProcess(new Chat2b2tCountProcess(this));
        if (ChatBot.getConfig().isQuotes())
            getProcessManager().addProcess(new QuoteProcess(this));
        if (ChatBot.getConfig().isNumberCount())
            getProcessManager().addProcess(new NumberCountProcess(this));
        if (ChatBot.getConfig().isKillAura())
            getProcessManager().addProcess(new KillAuraProcess(this));
    }

    public void connect() {
        this.commandManager.init();
        GeneralHelper.print("Setting client version to " + ProtocolHandler.getCurrent().getName() + " (" + ProtocolHandler.getCurrent().getProtocolVer() + ")", ChatMessage.TextColor.AQUA);
        GeneralHelper.print("Sending Handshake and LoginStart packets...", ChatMessage.TextColor.GREEN);
        sendPacket(new ServerBoundHandshakePacket(ProtocolHandler.getCurrent().getProtocolVer(), getMinecraftServerAddress().getIp(), getMinecraftServerAddress().getPort(), ServerBoundHandshakePacket.LOGIN_STATE));
        sendPacket(new ServerBoundLoginStartPacket(getSession().getUsername(), keyContainer == null ? null : keyContainer.publicKey()));
    }

    public boolean contactSessionServers(String serverHash) {
        JsonObject request = new JsonObject();
        request.addProperty("accessToken", getSession().getAccessToken());
        request.addProperty("selectedProfile", getSession().getUuid());
        request.addProperty("serverId", serverHash);
        Map<String, String> header = new HashMap<>();
        header.put("Content-Type", "application/json");

        GeneralHelper.HttpResponse resp = GeneralHelper.httpRequest(getAuthServersUrl() + "/session/minecraft/join", request.toString(), header, "POST");
        if (resp.responseCode() != 204) {
            this.minecraftAccount.setLoginAgain(true);
            close();
            return false;
        }
        return true;
    }

    private String getAuthServersUrl() {
        if (getSession().getAccountType() == Session.AccountType.ALTENING)
            return "http://sessionserver.thealtening.com";
        else
            return "https://sessionserver.mojang.com";
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
            GeneralHelper.print("Client disconnected, reconnecting in " + ChatBot.getConfig().getReconnectDelay() + " seconds...", ChatMessage.TextColor.RED);
            try {
                Thread.sleep(ChatBot.getConfig().getReconnectDelay() * 1000L);
                ChatBot.createConnection(getMinecraftServerAddress(), session, minecraftAccount);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void updateTranslations() {
        Translator.setTranslation(ChatBot.getConfig().getLocale());
    }

    public void sendChat(String message) {
        Instant instant = Instant.now();
        if (message.startsWith("/") && ProtocolHandler.getCurrent().getProtocolVer() > ProtocolHandler.getVersionFromName("1.18.2").getProtocolVer()) {
            SaltAndSig.SaltAndSigs saltAndSigs = SaltAndSig.SaltAndSigs.EMPTY;
            sendPacket(new ServerBoundCommandPacket(message.substring(1), instant, saltAndSigs));
            return;
        }
        ChatBot.getClientConnection().sendPacket(new ServerBoundChatPacket(message, instant, KeyHelper.generateSaltAndSig(instant, message)));
    }

    public void tick() {
        while (isConnected()) {
            if (tickWatch.hasPassed(50)) {//only tick 20 times per second, just like normal mc
                if (isInGame())
                    getClientPlayer().tick();
                try {
                    getProcessManager().tick();
                } catch (Exception e) {
                }
                new EventTick().run(this);
                tickWatch.reset();
            }
        }
    }

    public void sendPacket(Packet packet) {
        if (packet.getPacketId() == -1)
            return;
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

    public MinecraftServerAddress getMinecraftServerAddress() {
        return minecraftServerAddress;
    }

    public Session getSession() {
        return session;
    }

    public ClientPlayer getClientPlayer() {
        return clientPlayer;
    }

    public World getWorld() {
        return world;
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

    public PlayerInfoManager getPlayerManager() {
        return playerInfoManager;
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

    public KeyContainer getKeyContainer() {
        return keyContainer;
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
