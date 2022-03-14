package me.dustin.chatbot.process;

import me.dustin.chatbot.ChatBot;
import me.dustin.chatbot.helper.StopWatch;
import me.dustin.chatbot.network.ClientConnection;
import me.dustin.chatbot.network.packet.c2s.play.ServerBoundChatPacket;

public abstract class ChatBotProcess {

    private final ClientConnection clientConnection;
    private final StopWatch messageStopWatch = new StopWatch();

    public ChatBotProcess(ClientConnection clientConnection) {
        this.clientConnection = clientConnection;
    }

    public abstract void init();
    public abstract void tick();
    public abstract void stop();

    public ClientConnection getClientConnection() {
        return clientConnection;
    }

    public void sendChat(String message) {
        if (!messageStopWatch.hasPassed(ChatBot.getConfig().getMessageDelay()))
            return;
        messageStopWatch.reset();
        getClientConnection().sendPacket(new ServerBoundChatPacket((ChatBot.getConfig().isGreenText() && !message.startsWith("/") ? ">" : "") + message));
    }
}
