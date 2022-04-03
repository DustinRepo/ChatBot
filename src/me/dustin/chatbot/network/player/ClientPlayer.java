package me.dustin.chatbot.network.player;

import me.dustin.chatbot.ChatBot;
import me.dustin.chatbot.helper.StopWatch;
import me.dustin.chatbot.network.ClientConnection;
import me.dustin.chatbot.network.packet.c2s.play.ServerBoundChatPacket;

import java.util.UUID;

public class ClientPlayer {

    private final UUID uuid;
    private final String name;
    private final ClientConnection clientConnection;

    private int entityId;
    private double x,y,z;
    private float yaw, pitch;

    private final StopWatch messageStopwatch = new StopWatch();

    private String lastMessage = "";

    public ClientPlayer(String name, UUID uuid, ClientConnection clientConnection) {
        this.name = name;
        this.uuid = uuid;
        this.clientConnection = clientConnection;
    }

    public void tick() {
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
        this.yaw = yaw;
    }

    public float getPitch() {
        return pitch;
    }

    public void setPitch(float pitch) {
        this.pitch = pitch;
    }

    public void moveYaw(float yaw) {
        this.yaw += yaw;
    }

    public void movePitch(float pitch) {
        this.pitch += pitch;
    }

    public int getEntityId() {
        return entityId;
    }

    public void setEntityId(int entityId) {
        this.entityId = entityId;
    }
}
