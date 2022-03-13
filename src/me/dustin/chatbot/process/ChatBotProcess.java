package me.dustin.chatbot.process;

import me.dustin.chatbot.network.ClientConnection;

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
}
