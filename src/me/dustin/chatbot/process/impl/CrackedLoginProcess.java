package me.dustin.chatbot.process.impl;

import me.dustin.chatbot.ChatBot;
import me.dustin.chatbot.chat.ChatMessage;
import me.dustin.chatbot.event.EventReceiveChatMessage;
import me.dustin.chatbot.network.ClientConnection;
import me.dustin.chatbot.network.packet.c2s.play.ServerBoundChatPacket;
import me.dustin.chatbot.process.ChatBotProcess;
import me.dustin.events.core.EventListener;
import me.dustin.events.core.annotate.EventPointer;

public class CrackedLoginProcess extends ChatBotProcess {
    public CrackedLoginProcess(ClientConnection clientConnection) {
        super(clientConnection);
    }

    @Override
    public void init() {
        getClientConnection().getEventManager().register(this);
    }

    @EventPointer
    private final EventListener<EventReceiveChatMessage> eventReceiveChatMessageEventListener = new EventListener<>(event -> {
        ChatMessage chatMessage = event.getChatMessagePacket().getMessage();
        if (chatMessage.getBody().contains("/register")) {
            getClientConnection().sendPacket(new ServerBoundChatPacket("/register " + ChatBot.getConfig().getCrackedLoginPassword() + " " + ChatBot.getConfig().getCrackedLoginPassword()));
        } else if (chatMessage.getBody().contains("/login")) {
            getClientConnection().sendPacket(new ServerBoundChatPacket("/login " + ChatBot.getConfig().getCrackedLoginPassword()));
        }
    });

    @Override
    public void tick() {

    }

    @Override
    public void stop() {
        getClientConnection().getEventManager().unregister(this);
    }
}
