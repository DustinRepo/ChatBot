package me.dustin.chatbot.process.impl;

import me.dustin.chatbot.ChatBot;
import me.dustin.chatbot.helper.Timer;
import me.dustin.chatbot.network.ClientConnection;
import me.dustin.chatbot.network.packet.c2s.play.ServerBoundClientSettingsPacket;
import me.dustin.chatbot.process.ChatBotProcess;

import java.util.Random;

public class SkinBlinkProcess extends ChatBotProcess {
    private final Timer timer = new Timer();
    public SkinBlinkProcess(ClientConnection clientConnection) {
        super(clientConnection);
    }

    @Override
    public void init() {
        getClientConnection().getEventManager().register(this);
    }

    @Override
    public void tick() {
        if (timer.hasPassed(ChatBot.getConfig().getSkinBlinkDelay())) {
            Random random = new Random();
            int enabledSkinParts = 0;
            for (ServerBoundClientSettingsPacket.SkinPart part : ServerBoundClientSettingsPacket.SkinPart.values()) {
                if (random.nextBoolean())
                    enabledSkinParts |= part.getBitFlag();
            }
            getClientConnection().sendPacket(new ServerBoundClientSettingsPacket(ChatBot.getConfig().getLocale(), ChatBot.getConfig().isAllowServerListing(), enabledSkinParts));
            timer.reset();
        }
    }

    @Override
    public void stop() {
        getClientConnection().getEventManager().unregister(this);
    }
}
