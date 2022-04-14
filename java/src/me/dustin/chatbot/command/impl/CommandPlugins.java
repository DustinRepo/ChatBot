package me.dustin.chatbot.command.impl;

import me.dustin.chatbot.command.Command;
import me.dustin.chatbot.network.packet.impl.play.c2s.ServerBoundTabCompletePacket;
import me.dustin.chatbot.network.packet.impl.play.s2c.ClientBoundTabCompletePacket;
import me.dustin.events.core.EventListener;
import me.dustin.events.core.annotate.EventPointer;

import java.util.ArrayList;
import java.util.StringJoiner;
import java.util.UUID;

public class CommandPlugins extends Command {

    public CommandPlugins() {
        super("plugins");
    }
    private UUID sender;
    @Override
    public void run(String str, UUID sender) {
        this.sender = sender;
        getClientConnection().sendPacket(new ServerBoundTabCompletePacket(0, "/"));
        getClientConnection().getEventManager().register(this);
    }

    @EventPointer
    private final EventListener<ClientBoundTabCompletePacket> eventReceiveTabCompleteEventListener = new EventListener<>(packet -> {
        ArrayList<String> plugins = new ArrayList<>();
        packet.getMatches().forEach(tabCompleteMatch -> {
            String cmd = tabCompleteMatch.match();
            String pluginName = cmd.split(":")[0];
            if (cmd.contains(":") && !pluginName.equalsIgnoreCase("minecraft") && !pluginName.equalsIgnoreCase("bukkit") && !plugins.contains(pluginName)) {
                plugins.add(pluginName);
            }
        });
        StringJoiner sj = new StringJoiner(", ");
        plugins.forEach(sj::add);
        String message = "Plugins: (" + plugins.size() + ") " + sj.toString();
        if (plugins.size() == 0)
            message = "I couldn't find any plugins.";
        sendChat(message, sender);
        getClientConnection().getEventManager().unregister(this);
    });

}
