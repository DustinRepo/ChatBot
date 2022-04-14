package me.dustin.chatbot.entity;

import me.dustin.chatbot.ChatBot;
import me.dustin.chatbot.process.ChatBotProcess;

public class LivingEntity {
    protected final int entityId;
    protected double x, y, z;
    protected float yaw, pitch;

    public LivingEntity(int entityId, double x, double y, double z, float yaw, float pitch) {
        this.entityId = entityId;
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
    }

    public int getEntityId() {
        return entityId;
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

    public double distanceToBot() {
        double meX = ChatBot.getClientConnection().getClientPlayer().getX();
        double meY = ChatBot.getClientConnection().getClientPlayer().getY();
        double meZ = ChatBot.getClientConnection().getClientPlayer().getZ();
        double f = getX() - meX;
        double g = getY() - meY;
        double h = getZ() - meZ;
        return Math.sqrt(f * f + g * g + h * h);
    }

    @Override
    public String toString() {
        return "LivingEntity{" +
                "entityId=" + entityId +
                ", x=" + x +
                ", y=" + y +
                ", z=" + z +
                ", yaw=" + yaw +
                ", pitch=" + pitch +
                '}';
    }
}
