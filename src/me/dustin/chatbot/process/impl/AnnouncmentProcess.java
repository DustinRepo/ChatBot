package me.dustin.chatbot.process.impl;

import me.dustin.chatbot.ChatBot;
import me.dustin.chatbot.network.ClientConnection;
import me.dustin.chatbot.network.packet.c2s.play.ServerBoundChatPacket;
import me.dustin.chatbot.process.ChatBotProcess;

import java.util.Random;

public class AnnouncmentProcess extends ChatBotProcess {
    private long lastAnnouncement;
    public AnnouncmentProcess(ClientConnection clientConnection) {
        super(clientConnection);
    }
    private final String[] announcements = new String[]{
            "Use {PREFIX}help to get a list of my commands",
            "I can try to grab the server's plugins! use !plugins",
            "I can get you a player's skin directly from Minecraft! Use !skin <name>",
            "Use {PREFIX}coinflip to flip a coin",
            "Use {PREFIX}worstping or {PREFIX}bestping to see who has the lowest/highest ping", "Use {PREFIX}coffee to get a picture of coffee",
            "Need to report someone? Use {PREFIX}report <name> <reason>",
            "Use {PREFIX}isEven to see if a number is even!",
            "Need to see server TPS? {PREFIX}tps",
            "Want to use this bot program? https://github.com/DustinRepo/ChatBot"
    };

    @Override
    public void init() {
        lastAnnouncement = System.currentTimeMillis();
    }

    @Override
    public void tick() {
        if (System.currentTimeMillis() - lastAnnouncement >= ChatBot.getConfig().getAnnouncementDelay() * 1000L && getClientConnection().getNetworkState() == ClientConnection.NetworkState.PLAY) {
            int size = announcements.length;
            Random random = new Random();
            int select = random.nextInt(size);
            getClientConnection().sendPacket(new ServerBoundChatPacket((ChatBot.getConfig().isGreenText() ? ">" : "") + announcements[select].replace("{PREFIX}", ChatBot.getConfig().getCommandPrefix())));
            lastAnnouncement = System.currentTimeMillis();
        }
    }

    @Override
    public void stop() {

    }
}
