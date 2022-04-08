package me.dustin.chatbot.command.impl;

import me.dustin.chatbot.command.Command;
import me.dustin.chatbot.event.EventReceiveTabComplete;
import me.dustin.chatbot.network.packet.c2s.play.ServerBoundTabCompletePacket;
import me.dustin.chatbot.network.packet.s2c.play.ClientBoundTabCompletePacket;
import me.dustin.events.core.EventListener;
import me.dustin.events.core.annotate.EventPointer;

import java.util.ArrayList;
import java.util.StringJoiner;
import java.util.UUID;

public class CommandPlugins extends Command {

    public CommandPlugins() {
        super("plugins");
    }

    @Override
    public void run(String str, UUID sender) {
        getClientConnection().sendPacket(new ServerBoundTabCompletePacket(0, "/"));
        getClientConnection().getEventManager().register(this);
    }

    @EventPointer
    private final EventListener<EventReceiveTabComplete> eventReceiveTabCompleteEventListener = new EventListener<>(event -> {
        ArrayList<String> plugins = new ArrayList<>();
        event.getPacket().getMatches().forEach(tabCompleteMatch -> {
            String cmd = tabCompleteMatch.match();
            String pluginName = cmd.split(":")[0];
            if (cmd.contains(":") && !pluginName.equalsIgnoreCase("minecraft") && !pluginName.equalsIgnoreCase("bukkit") && !plugins.contains(pluginName)) {
                plugins.add(pluginName);
            }
        });
        StringJoiner sj = new StringJoiner(", ");
        plugins.forEach(sj::add);
        String message = "Plugins: (" + plugins.size() + ") " + sj.toString();
        sendChat(message);
        getClientConnection().getEventManager().unregister(this);
    });

}
