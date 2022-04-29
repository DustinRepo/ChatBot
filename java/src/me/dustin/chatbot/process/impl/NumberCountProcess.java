package me.dustin.chatbot.process.impl;

import me.dustin.chatbot.event.EventReceiveChatMessage;
import me.dustin.chatbot.helper.GeneralHelper;
import me.dustin.chatbot.helper.MCAPIHelper;
import me.dustin.chatbot.network.ClientConnection;
import me.dustin.chatbot.process.ChatBotProcess;
import me.dustin.events.core.EventListener;
import me.dustin.events.core.annotate.EventPointer;

import java.util.StringJoiner;

public class NumberCountProcess extends ChatBotProcess {

    public NumberCountProcess(ClientConnection clientConnection) {
        super(clientConnection);
    }

    @Override
    public void init() {
        getClientConnection().getEventManager().register(this);
    }

    @EventPointer
    private final EventListener<EventReceiveChatMessage> eventReceiveChatMessageEventListener = new EventListener<>(event -> {
        if (event.getChatMessagePacket().getSender() == null || GeneralHelper.matchUUIDs(event.getChatMessagePacket().getSender().toString(), getClientConnection().getSession().getUuid()))
            return;
        String senderName = MCAPIHelper.getNameFromUUID(event.getChatMessagePacket().getSender().uuid());
        String[] split = event.getChatMessagePacket().getMessage().getBody().split(" ");

        if (senderName.isEmpty())
            return;
        int num = 0;
        int count = 0;
        StringJoiner sj = new StringJoiner(" + ");
        for (String s : split) {
            try {
                int n = Integer.parseInt(s);
                sj.add(s);
                num += n;
                count++;
            } catch (NumberFormatException e) {
            }
        }
        if (count > 1)
            if (num == 420) {
                sendChat("All of the numers in " + senderName + "'s message add to 420! " + sj.toString() + " = 420");
            } else if (num == 69) {
                sendChat("All of the numers in " + senderName + "'s message add to 69! " + sj.toString() + " = 69");
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
