package me.dustin.chatbot.process.impl;

import me.dustin.chatbot.ChatBot;
import me.dustin.chatbot.event.EventReceiveChatMessage;
import me.dustin.chatbot.helper.GeneralHelper;
import me.dustin.chatbot.helper.StopWatch;
import me.dustin.chatbot.network.ClientConnection;
import me.dustin.chatbot.network.packet.c2s.play.ServerBoundChatPacket;
import me.dustin.chatbot.network.packet.s2c.play.ClientBoundChatMessagePacket;
import me.dustin.chatbot.process.ChatBotProcess;
import me.dustin.events.core.EventListener;
import me.dustin.events.core.annotate.EventPointer;
import me.dustin.events.core.priority.Priority;

public class Chat2b2tProcess extends ChatBotProcess {

    private int chatCount;
    private final StopWatch stopWatch = new StopWatch();
    public Chat2b2tProcess(ClientConnection clientConnection) {
        super(clientConnection);
    }

    @Override
    public void init() {
        getClientConnection().getEventManager().register(this);
    }

    @EventPointer
    private final EventListener<EventReceiveChatMessage> eventReceiveChatMessageEventListener = new EventListener<>(event -> {
        if (event.isCancelled())
            return;
        String body = GeneralHelper.strip(event.getChatMessagePacket().getMessage().getBody().toLowerCase());
        System.out.println(body);
       if (stopWatch.hasPassed(ChatBot.getConfig().getMessageDelay()) && (body.contains("2b2t") || body.contains("2b") || body.contains("2builders2tools") || body.contains("2 builders 2 tools") || body.contains("oldest anarchy server in minecraft")) && !GeneralHelper.matchUUIDs(event.getChatMessagePacket().getSender().toString(), getClientConnection().getSession().getUuid())) {
           getClientConnection().sendPacket(new ServerBoundChatPacket((ChatBot.getConfig().isGreenText() ? ">" : "") + "Congrats. We only made it " + chatCount + " messages before someone mentioned 2b2t."));
           chatCount = 0;
           stopWatch.reset();
       } else {
           chatCount++;
       }
    }, Priority.LAST);

    @Override
    public void tick() {

    }

    @Override
    public void stop() {
        getClientConnection().getEventManager().unregister(this);
    }
}
