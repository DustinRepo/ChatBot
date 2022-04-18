package me.dustin.chatbot.process.impl;

import me.dustin.chatbot.ChatBot;
import me.dustin.chatbot.helper.StopWatch;
import me.dustin.chatbot.network.ClientConnection;
import me.dustin.chatbot.network.packet.impl.play.c2s.ServerBoundPlayerRotationPacket;
import me.dustin.chatbot.network.packet.impl.play.c2s.ServerBoundPlayerSwingPacket;
import me.dustin.chatbot.process.ChatBotProcess;

import java.util.Random;

public class AntiAFKProcess extends ChatBotProcess {
    private final StopWatch stopWatch = new StopWatch();
    public AntiAFKProcess(ClientConnection clientConnection) {
        super(clientConnection);
    }

    @Override
    public void init() {
        stopWatch.reset();
    }

    @Override
    public void tick() {
        if (getClientConnection().getClientPlayer().hasSetPos() && stopWatch.hasPassed(ChatBot.getConfig().getAntiAFKDelay() * 1000L)) {
            Random random = new Random();
            float yaw = random.nextInt(360) - 180;
            float pitch = random.nextInt(180) - 90;
            getClientConnection().getClientPlayer().setYaw(yaw);
            getClientConnection().getClientPlayer().setPitch(pitch);
            getClientConnection().sendPacket(new ServerBoundPlayerSwingPacket(ServerBoundPlayerSwingPacket.MAIN_HAND));
            getClientConnection().sendPacket(new ServerBoundPlayerRotationPacket(yaw, pitch, true));
            stopWatch.reset();
        }
    }

    @Override
    public void stop() {}

    public StopWatch getStopWatch() {
        return stopWatch;
    }
}
