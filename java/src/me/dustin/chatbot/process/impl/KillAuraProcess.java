package me.dustin.chatbot.process.impl;

import me.dustin.chatbot.ChatBot;
import me.dustin.chatbot.entity.Entity;
import me.dustin.chatbot.entity.LivingEntity;
import me.dustin.chatbot.entity.player.PlayerEntity;
import me.dustin.chatbot.entity.player.PlayerInfo;
import me.dustin.chatbot.helper.StopWatch;
import me.dustin.chatbot.network.ClientConnection;
import me.dustin.chatbot.network.packet.impl.play.c2s.ServerBoundInteractEntityPacket;
import me.dustin.chatbot.network.packet.impl.play.c2s.ServerBoundPlayerSwingPacket;
import me.dustin.chatbot.entity.player.ClientPlayer;
import me.dustin.chatbot.process.ChatBotProcess;

public class KillAuraProcess extends ChatBotProcess {
    private final StopWatch stopWatch = new StopWatch();
    public KillAuraProcess(ClientConnection clientConnection) {
        super(clientConnection);
    }

    @Override
    public void init() {
        stopWatch.reset();
    }

    @Override
    public void tick() {
        long delay = (long) (1000L / ChatBot.getConfig().getKillAuraCPS());
        LivingEntity livingEntity = getClosest();
        if (livingEntity == null)
            return;
        float[] rotations = rotateTo(livingEntity);
        ClientPlayer clientPlayer = getClientConnection().getClientPlayer();
        clientPlayer.setYaw(rotations[0]);
        clientPlayer.setPitch(rotations[1]);
        if (stopWatch.hasPassed(delay)) {
            ChatBot.getClientConnection().sendPacket(new ServerBoundInteractEntityPacket(livingEntity, ServerBoundInteractEntityPacket.ATTACK));
            ChatBot.getClientConnection().sendPacket(new ServerBoundPlayerSwingPacket(ServerBoundPlayerSwingPacket.MAIN_HAND));
            stopWatch.reset();
            AntiAFKProcess antiAFKProcess = getClientConnection().getProcessManager().get(AntiAFKProcess.class);
            if (antiAFKProcess != null)
                antiAFKProcess.getStopWatch().reset();
        }
    }

    private LivingEntity getClosest() {
        LivingEntity living = null;
        double distance = 7;
        for (Entity entity : getClientConnection().getWorld().getEntities()) {
            if (entity instanceof LivingEntity livingEntity) {
                if (livingEntity instanceof PlayerEntity player && (player.getGameMode() == PlayerInfo.GameMode.CREATIVE || player.getGameMode() == PlayerInfo.GameMode.SPECTATOR))
                    continue;
                double distanceToBot = livingEntity.distanceToBot();
                if (distanceToBot > ChatBot.getConfig().getKillAuraRange())
                    continue;
                if (living == null || distanceToBot < distance) {
                    living = livingEntity;
                    distance = distanceToBot;
                }
            }
        }
        return living;
    }

    private float[] rotateTo(LivingEntity livingEntity) {
        double xDif = livingEntity.getX() - getClientConnection().getClientPlayer().getX();
        double zDif = livingEntity.getZ() - getClientConnection().getClientPlayer().getZ();
        double yDif = (livingEntity.getY() + (livingEntity instanceof PlayerEntity ? 1.65 : 1)) - (getClientConnection().getClientPlayer().getY() + 1.65);

        double dist2D = Math.sqrt(xDif * xDif + zDif * zDif);
        float yaw = (float) (Math.atan2(zDif, xDif) * 180.0D / Math.PI) - 90.0F;
        float pitch = (float) (-(Math.atan2(yDif, dist2D) * 180.0D / Math.PI));
        return new float[]{wrapDegrees(yaw), wrapDegrees(pitch)};
    }

    private float wrapDegrees(float degrees) {
        float f = degrees % 360.0f;
        if (f >= 180.0f) {
            f -= 360.0f;
        }
        if (f < -180.0f) {
            f += 360.0f;
        }
        return f;
    }

    @Override
    public void stop() {

    }
}
