package me.dustin.chatbot.process.impl;

import me.dustin.chatbot.ChatBot;
import me.dustin.chatbot.helper.Timer;
import me.dustin.chatbot.network.ClientConnection;
import me.dustin.chatbot.network.packet.c2s.play.ServerBoundPlayerRotationPacket;
import me.dustin.chatbot.network.packet.c2s.play.ServerBoundPlayerSwingPacket;
import me.dustin.chatbot.process.ChatBotProcess;

import java.util.Random;

public class AntiAFKProcess extends ChatBotProcess {
    private final Timer timer = new Timer();
    public AntiAFKProcess(ClientConnection clientConnection) {
        super(clientConnection);
    }

    @Override
    public void init() {
        timer.reset();
    }

    @Override
    public void tick() {
        if (timer.hasPassed(ChatBot.getConfig().getAntiAFKDelay() * 1000L)) {
            Random random = new Random();
            float yaw = random.nextInt(360) - 180;
            float pitch = random.nextInt(180) - 90;
            getClientConnection().sendPacket(new ServerBoundPlayerRotationPacket(yaw, pitch, true));
            getClientConnection().sendPacket(new ServerBoundPlayerSwingPacket(ServerBoundPlayerSwingPacket.MAIN_HAND));
            timer.reset();
        }
    }

    @Override
    public void stop() {}
}
