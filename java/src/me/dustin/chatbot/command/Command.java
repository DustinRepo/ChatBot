package me.dustin.chatbot.command;

import me.dustin.chatbot.ChatBot;
import me.dustin.chatbot.network.ClientConnection;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public abstract class Command {

    private final String name;
    private final List<String> alias = new ArrayList<>();

    private boolean directMessage;

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
        return ChatBot.getClientConnection();
    }

    public void setDirectMessage(boolean directMessage) {
        this.directMessage = directMessage;
    }

    public boolean isDirectMessage() {
        return directMessage;
    }

    public void sendChat(String message, UUID sender) {
        if (isDirectMessage() && !message.startsWith("/"))
            getClientConnection().getClientPlayer().chat("/msg " + getClientConnection().getPlayerManager().get(sender).getName() + " " + message);
        else
            getClientConnection().getClientPlayer().chat(message);
    }
}
