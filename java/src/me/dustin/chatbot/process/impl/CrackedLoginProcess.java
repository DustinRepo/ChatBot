package me.dustin.chatbot.process.impl;

import me.dustin.chatbot.ChatBot;
import me.dustin.chatbot.chat.ChatMessage;
import me.dustin.chatbot.event.EventReceiveChatMessage;
import me.dustin.chatbot.helper.KeyHelper;
import me.dustin.chatbot.network.ClientConnection;
import me.dustin.chatbot.network.key.SaltAndSig;
import me.dustin.chatbot.network.packet.impl.play.c2s.ServerBoundChatPacket;
import me.dustin.chatbot.process.ChatBotProcess;
import me.dustin.events.core.EventListener;
import me.dustin.events.core.annotate.EventPointer;

import java.time.Instant;

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
        for (String s : ChatBot.getConfig().getPasswordKeywords()) {
            if (chatMessage.getBody().contains(s)) {
                String message = ChatBot.getConfig().getPasswordCreateCommand() + " " + ChatBot.getConfig().getCrackedLoginPassword() + (ChatBot.getConfig().isPasswordCreateUseTwice() ? " " + ChatBot.getConfig().getCrackedLoginPassword() : "");
                ChatBot.getClientConnection().sendChat(message);
                stop();
                return;
            }
        }
        for (String s : ChatBot.getConfig().getLoginKeywords()) {
            if (chatMessage.getBody().contains(s)) {
                String message = ChatBot.getConfig().getLoginCommand() + " " + ChatBot.getConfig().getCrackedLoginPassword();
                ChatBot.getClientConnection().sendChat(message);
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
