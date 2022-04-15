package me.dustin.chatbot.entity.player;

import me.dustin.chatbot.entity.LivingEntity;

import java.util.UUID;

public class PlayerEntity extends LivingEntity {
    private final PlayerInfo playerInfo;
    public PlayerEntity(int entityId, double x, double y, double z, float yaw, float pitch, PlayerInfo playerInfo) {
        super(entityId, "entity.minecraft.player", x, y, z, yaw, pitch);
        this.playerInfo = playerInfo;
    }

    public String getName() {
        return getPlayerData().getName();
    }

    public PlayerInfo.GameMode getGameMode() {
        return getPlayerData().getGameMode();
    }

    public UUID getUUID() {
        return getPlayerData().getUuid();
    }

    public PlayerInfo getPlayerData() {
        return playerInfo;
    }
    @Override
    public String toString() {
        return "PlayerEntity{" +
                "entityId=" + entityId +
                ", x=" + x +
                ", y=" + y +
                ", z=" + z +
                ", yaw=" + yaw +
                ", pitch=" + pitch +
                '}';
    }
}
