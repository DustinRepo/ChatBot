package me.dustin.chatbot.entity;

public class LivingEntity extends Entity{

    public LivingEntity(int entityId, String type, double x, double y, double z, float yaw, float pitch) {
        super(entityId, type, x, y, z, yaw, pitch);
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
