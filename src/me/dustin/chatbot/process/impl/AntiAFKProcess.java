package me.dustin.chatbot.process.impl;

import me.dustin.chatbot.ChatBot;
import me.dustin.chatbot.network.ClientConnection;
import me.dustin.chatbot.network.packet.c2s.play.ServerBoundPlayerRotationPacket;
import me.dustin.chatbot.network.packet.c2s.play.ServerBoundPlayerSwingPacket;
import me.dustin.chatbot.process.ChatBotProcess;

import java.util.Random;

public class AntiAFKProcess extends ChatBotProcess {
    private long lastAttempt = System.currentTimeMillis();

    public AntiAFKProcess(ClientConnection clientConnection) {
        super(clientConnection);
    }

    @Override
    public void init() {
        lastAttempt = System.currentTimeMillis();
    }

    @Override
    public void tick() {
        if (System.currentTimeMillis() - lastAttempt >= ChatBot.getConfig().getAntiAFKDelay() * 1000L) {
            Random random = new Random();
            float yaw = random.nextInt(360) - 180;
            float pitch = random.nextInt(180) - 90;
            getClientConnection().sendPacket(new ServerBoundPlayerRotationPacket(yaw, pitch, true));
            getClientConnection().sendPacket(new ServerBoundPlayerSwingPacket(ServerBoundPlayerSwingPacket.MAIN_HAND));
            lastAttempt = System.currentTimeMillis();
        }
    }

    @Override
    public void stop() {}
}
