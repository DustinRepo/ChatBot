package me.dustin.chatbot.process.impl;

import me.dustin.chatbot.ChatBot;
import me.dustin.chatbot.helper.StopWatch;
import me.dustin.chatbot.network.ClientConnection;
import me.dustin.chatbot.network.packet.ProtocolHandler;
import me.dustin.chatbot.network.packet.c2s.play.ServerBoundClientSettingsPacket;
import me.dustin.chatbot.process.ChatBotProcess;

import java.util.Random;

public class SkinBlinkProcess extends ChatBotProcess {
    private final StopWatch stopWatch = new StopWatch();
    public SkinBlinkProcess(ClientConnection clientConnection) {
        super(clientConnection);
    }

    @Override
    public void init() {
        if (ProtocolHandler.getCurrent().getProtocolVer() <= ProtocolHandler.getVersionFromName("1.8.9").getProtocolVer())
            return;
        getClientConnection().getEventManager().register(this);
    }

    @Override
    public void tick() {
        if (stopWatch.hasPassed(ChatBot.getConfig().getSkinBlinkDelay())) {
            Random random = new Random();
            int enabledSkinParts = 0;
            for (ServerBoundClientSettingsPacket.SkinPart part : ServerBoundClientSettingsPacket.SkinPart.values()) {
                if (random.nextBoolean())
                    enabledSkinParts |= part.getBitFlag();
            }
            getClientConnection().sendPacket(new ServerBoundClientSettingsPacket(ChatBot.getConfig().getLocale(), ChatBot.getConfig().isAllowServerListing(), enabledSkinParts));
            stopWatch.reset();
        }
    }

    @Override
    public void stop() {
        if (ProtocolHandler.getCurrent().getProtocolVer() <= ProtocolHandler.getVersionFromName("1.8.9").getProtocolVer())
            return;
        getClientConnection().getEventManager().unregister(this);
    }
}
