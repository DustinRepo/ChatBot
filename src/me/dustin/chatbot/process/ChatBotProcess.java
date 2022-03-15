package me.dustin.chatbot.process;

import me.dustin.chatbot.ChatBot;
import me.dustin.chatbot.helper.StopWatch;
import me.dustin.chatbot.network.ClientConnection;
import me.dustin.chatbot.network.packet.c2s.play.ServerBoundChatPacket;

public abstract class ChatBotProcess {

    private final ClientConnection clientConnection;

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
        getClientConnection().getClientPlayer().chat(message);
    }
}
