package me.dustin.chatbot.entity.player;

import me.dustin.chatbot.ChatBot;
import me.dustin.chatbot.entity.player.PlayerInfo;
import me.dustin.chatbot.helper.StopWatch;
import me.dustin.chatbot.network.ClientConnection;
import me.dustin.chatbot.network.packet.ProtocolHandler;
import me.dustin.chatbot.network.packet.impl.play.c2s.*;

import java.util.UUID;

public class ClientPlayer {

    private UUID uuid;
    private String name;
    private final ClientConnection clientConnection;

    private int entityId;
    private double x,y,z;
    private float yaw, pitch;
    private float lastYaw, lastPitch;
    private int ticks;

    private PlayerInfo.GameMode gameMode = PlayerInfo.GameMode.SURVIVAL;

    private final StopWatch messageStopwatch = new StopWatch();

    private String lastMessage = "";
    private final boolean below1_9;
    public ClientPlayer(String name, UUID uuid, ClientConnection clientConnection) {
        this.name = name;
        this.uuid = uuid;
        this.clientConnection = clientConnection;
        below1_9 = ProtocolHandler.getCurrent().getProtocolVer() < ProtocolHandler.getVersionFromName("1.9.1-pre1").getProtocolVer();
    }

    public void tick() {
        if (below1_9) {

            if (ticks % 20 == 0) {
                if (lastYaw != yaw || lastPitch != pitch)
                    getClientConnection().sendPacket(new ServerBoundPlayerPositionAndRotationPacket(getX(), getY(), getZ(), getYaw(), getPitch(),true));
                else
                    getClientConnection().sendPacket(new ServerBoundPlayerPositionPacket(getX(), getY(), getZ(), true));
            } else {
                if (lastYaw != yaw || lastPitch != pitch)
                    getClientConnection().sendPacket(new ServerBoundPlayerPositionAndRotationPacket(getX(), getY(), getZ(), getYaw(), getPitch(),true));
                else
                    getClientConnection().sendPacket(new ServerBoundPlayerOnGroundPacket(true));
            }
        } else if (ticks % 20 == 0) {
            if (lastYaw != yaw || lastPitch != pitch)
                getClientConnection().sendPacket(new ServerBoundPlayerPositionAndRotationPacket(getX(), getY(), getZ(), getYaw(), getPitch(),true));
            else
                getClientConnection().sendPacket(new ServerBoundPlayerPositionPacket(getX(), getY(), getZ(), true));
        } else if (lastYaw != yaw || lastPitch != pitch) {
            getClientConnection().sendPacket(new ServerBoundPlayerRotationPacket(getYaw(), getPitch(), true));
        }
        ticks++;
    }

    public void chat(String message) {
        if ((!ChatBot.getConfig().isRepeatMessages() && lastMessage.equalsIgnoreCase(message)) || !messageStopwatch.hasPassed(ChatBot.getConfig().getMessageDelay())) {
            return;
        }
        getClientConnection().sendPacket(new ServerBoundChatPacket((ChatBot.getConfig().isGreenText() && !message.startsWith("/") ? ">" : "") + message));
        messageStopwatch.reset();
        lastMessage = message;
    }

    public ClientConnection getClientConnection() {
        return clientConnection;
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    public PlayerInfo.GameMode getGameMode() {
        return gameMode;
    }

    public void setGameMode(PlayerInfo.GameMode gameMode) {
        this.gameMode = gameMode;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getZ() {
        return z;
    }

    public void setZ(double z) {
        this.z = z;
    }

    public void moveX(double x) {
        this.x += x;
    }

    public void moveY(double y) {
        this.y += y;
    }

    public void moveZ(double z) {
        this.z += z;
    }

    public float getYaw() {
        return yaw;
    }

    public void setYaw(float yaw) {
        this.lastYaw = this.yaw;
        this.yaw = yaw;
    }

    public float getPitch() {
        return pitch;
    }

    public void setPitch(float pitch) {
        this.lastPitch = this.pitch;
        this.pitch = pitch;
    }

    public float getLastYaw() {
        return lastYaw;
    }

    public float getLastPitch() {
        return lastPitch;
    }

    public void moveYaw(float yaw) {
        this.lastYaw = this.yaw;
        this.yaw += yaw;
    }

    public void movePitch(float pitch) {
        this.lastPitch = this.pitch;
        this.pitch += pitch;
    }

    public int getEntityId() {
        return entityId;
    }

    public void setEntityId(int entityId) {
        this.entityId = entityId;
    }
}
