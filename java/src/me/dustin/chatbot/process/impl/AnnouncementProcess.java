package me.dustin.chatbot.process.impl;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import me.dustin.chatbot.ChatBot;
import me.dustin.chatbot.chat.ChatMessage;
import me.dustin.chatbot.command.CommandManager;
import me.dustin.chatbot.helper.GeneralHelper;
import me.dustin.chatbot.helper.StopWatch;
import me.dustin.chatbot.network.ClientConnection;
import me.dustin.chatbot.network.packet.c2s.play.ServerBoundChatPacket;
import me.dustin.chatbot.process.ChatBotProcess;

import java.io.File;
import java.util.ArrayList;
import java.util.Random;

public class AnnouncementProcess extends ChatBotProcess {
    private final StopWatch stopWatch = new StopWatch();
    private final ArrayList<String> announcements = new ArrayList<>();
    public AnnouncementProcess(ClientConnection clientConnection) {
        super(clientConnection);
        File customCommandsFile = new File(new File("").getAbsolutePath() + File.separator + "custom", "announcements.json");
        if (customCommandsFile.exists()) {
            JsonArray array = GeneralHelper.gson.fromJson(GeneralHelper.readFile(customCommandsFile), JsonArray.class);
            for (int i = 0; i < array.size(); i++) {
                announcements.add(array.get(i).getAsString());
            }
        } else {
            GeneralHelper.print("No announcement file found!", ChatMessage.TextColors.DARK_RED);
        }
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
            getClientConnection().getClientPlayer().chat(announcements.get(select).replace("{PREFIX}", ChatBot.getConfig().getCommandPrefix()));
            stopWatch.reset();
        }
    }

    @Override
    public void stop() {

    }
}
