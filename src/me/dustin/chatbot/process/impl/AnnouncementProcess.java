package me.dustin.chatbot.process.impl;

import me.dustin.chatbot.ChatBot;
import me.dustin.chatbot.helper.StopWatch;
import me.dustin.chatbot.network.ClientConnection;
import me.dustin.chatbot.network.packet.c2s.play.ServerBoundChatPacket;
import me.dustin.chatbot.process.ChatBotProcess;

import java.util.ArrayList;
import java.util.Random;

public class AnnouncementProcess extends ChatBotProcess {
    private final StopWatch stopWatch = new StopWatch();
    private final ArrayList<String> announcements = new ArrayList<>();
    public AnnouncementProcess(ClientConnection clientConnection) {
        super(clientConnection);
        announcements.add("Use {PREFIX}help to get a list of my commands");
        announcements.add("I can try to grab the server's plugins! use !plugins");
        announcements.add("I can get you a player's skin directly from Minecraft! Use !skin <name>");
        announcements.add("Use {PREFIX}coinflip to flip a coin");
        announcements.add("Use {PREFIX}worstping or {PREFIX}bestping to see who has the lowest/highest ping");
        announcements.add("Use {PREFIX}coffee to get a picture of coffee");
        announcements.add("Need to report someone? Use {PREFIX}report <name> <reason>");
        announcements.add("Use {PREFIX}isEven to see if a number is even!");
        announcements.add("Need to see server TPS? {PREFIX}tps");
        announcements.add("Want to use this bot program? It supports all versions from 1.12 - 1.18.2! https://github.com/DustinRepo/ChatBot");
        if (ChatBot.getConfig().is2b2tCount())
            announcements.add("Use {PREFIX}2b2tcount <name> to see how many times someone has mentioned 2b2t!");
        if (ChatBot.getConfig().isQuotes())
            announcements.add("Use {PREFIX}quote <name> to get a random quote from a player!");
    }

    @Override
    public void init() {
        stopWatch.reset();
    }

    @Override
    public void tick() {
        if (stopWatch.hasPassed(ChatBot.getConfig().getAnnouncementDelay() * 1000L) && getClientConnection().getNetworkState() == ClientConnection.NetworkState.PLAY) {
            int size = announcements.size();
            Random random = new Random();
            int select = random.nextInt(size);
            getClientConnection().sendPacket(new ServerBoundChatPacket((ChatBot.getConfig().isGreenText() ? ">" : "") + announcements.get(select).replace("{PREFIX}", ChatBot.getConfig().getCommandPrefix())));
            stopWatch.reset();
        }
    }

    @Override
    public void stop() {

    }
}
