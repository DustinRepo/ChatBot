package me.dustin.chatbot.command;

import me.dustin.chatbot.ChatBot;
import me.dustin.chatbot.network.ClientConnection;
import me.dustin.chatbot.network.packet.c2s.play.ServerBoundChatPacket;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public abstract class Command {

    private final String name;
    private final List<String> alias = new ArrayList<>();
    private ClientConnection clientConnection;

    public Command(String name) {
        this.name = name;
    }

    public abstract void run(String str, UUID sender);

    public String getName() {
        return name;
    }

    public List<String> getAlias() {
        return alias;
    }

    public ClientConnection getClientConnection() {
        return clientConnection;
    }

    public void setClientConnection(ClientConnection clientConnection) {
        this.clientConnection = clientConnection;
    }

    public void sendChat(String message) {
        getClientConnection().sendPacket(new ServerBoundChatPacket((ChatBot.getConfig().isGreenText() && !message.startsWith("/") ? ">" : "") + message));
    }
}
