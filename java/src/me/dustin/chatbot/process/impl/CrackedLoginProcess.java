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
        for (String s : ChatBot.getConfig().getLoginKeywords()) {
            if (chatMessage.getBody().contains(s)) {
                getClientConnection().sendPacket(new ServerBoundChatPacket(ChatBot.getConfig().getPasswordCreateCommand() + " " + ChatBot.getConfig().getCrackedLoginPassword() + (ChatBot.getConfig().isPasswordCreateUseTwice() ? " " + ChatBot.getConfig().getCrackedLoginPassword() : "")));
                stop();
                return;
            }
        }
        for (String s : ChatBot.getConfig().getLoginKeywords()) {
            if (chatMessage.getBody().contains(s)) {
                getClientConnection().sendPacket(new ServerBoundChatPacket(ChatBot.getConfig().getLoginCommand() + " " + ChatBot.getConfig().getCrackedLoginPassword()));
                stop();
                return;
            }
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
