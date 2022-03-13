package me.dustin.chatbot.command;

import me.dustin.chatbot.ChatBot;
import me.dustin.chatbot.event.EventReceiveChatMessage;
import me.dustin.chatbot.helper.ClassHelper;
import me.dustin.chatbot.helper.GeneralHelper;
import me.dustin.chatbot.helper.Timer;
import me.dustin.chatbot.network.ClientConnection;
import me.dustin.chatbot.network.packet.s2c.play.ClientBoundChatMessagePacket;
import me.dustin.chatbot.network.player.OtherPlayer;
import me.dustin.chatbot.network.player.PlayerManager;
import me.dustin.events.core.EventListener;
import me.dustin.events.core.annotate.EventPointer;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CommandManager {

    private final ArrayList<Command> commands = new ArrayList<>();
    private final ClientConnection clientConnection;
    private final Timer timer = new Timer();

    public CommandManager(ClientConnection clientConnection) {
        this.clientConnection = clientConnection;
    }

    public void init() {
        getClientConnection().getEventManager().unregister(this);
        getClientConnection().getEventManager().register(this);
        commands.clear();
        List<Class<?>> classes = ClassHelper.INSTANCE.getClasses("me.dustin.chatbot.command.impl", Command.class);
        classes.forEach(clazz -> {
            try {
                @SuppressWarnings("deprecation")
                Command instance = (Command) clazz.newInstance();
                instance.setClientConnection(clientConnection);
                getCommands().add(instance);
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
        });
    }

    @EventPointer
    private final EventListener<EventReceiveChatMessage> eventReceiveChatMessageEventListener = new EventListener<>(event -> {
        ClientBoundChatMessagePacket clientBoundChatMessagePacket = event.getChatMessagePacket();
        UUID sender = clientBoundChatMessagePacket.getSender();
        String string = GeneralHelper.strip(event.getChatMessagePacket().getMessage().getBody());
        String[] sA = string.split(" ");
        if (sA.length > 2 && sA[1].equalsIgnoreCase("whispers:")) {
            string = string.substring(sA[0].length() + sA[1].length() + 2);
            if (sender == null && getClientConnection().getPlayerManager().get(sA[1]) != null) {
                sender = getClientConnection().getPlayerManager().get(sA[1]).getUuid();
            }
        }
        if (!string.startsWith(ChatBot.getConfig().getCommandPrefix()) || sender.toString().equalsIgnoreCase(getClientConnection().getSession().getUuid())) {
            return;
        }
        if (!timer.hasPassed(ChatBot.getConfig().getMessageDelay())) {
            return;
        }
        try {
            String cmd = string.split(" ")[0].replace(ChatBot.getConfig().getCommandPrefix(), "");
            String input;
            if (string.contains(" "))
                input = string.replace(cmd + " ", "");
            else
                input = string.replace(cmd, "");
            input = input.substring(1);
            for (Command command : commands) {
                if (command.getName().equalsIgnoreCase(cmd) || command.getAlias().contains(cmd.toLowerCase())) {
                    try {
                        command.run(input, sender);
                        timer.reset();
                        return;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (IndexOutOfBoundsException e) {}
    });

    public ArrayList<Command> getCommands() {
        return commands;
    }

    public ClientConnection getClientConnection() {
        return clientConnection;
    }
}
