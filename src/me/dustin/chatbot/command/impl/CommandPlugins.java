package me.dustin.chatbot.command.impl;

import me.dustin.chatbot.command.Command;
import me.dustin.chatbot.network.packet.c2s.play.ServerBoundTabCompletePacket;
import me.dustin.chatbot.network.packet.s2c.play.ClientBoundTabCompletePacket;

import java.util.ArrayList;
import java.util.StringJoiner;
import java.util.UUID;

public class CommandPlugins extends Command {

    //TODO: better way to do this. I might just import Jex's event manager
    private static ClientBoundTabCompletePacket tabCompletePacket;

    public CommandPlugins() {
        super("plugins");
    }

    @Override
    public void run(String str, UUID sender) {
        getClientConnection().sendPacket(new ServerBoundTabCompletePacket(0, "/"));
        new Thread(() -> {
            long start = System.currentTimeMillis();
            while (tabCompletePacket == null) {
                if (System.currentTimeMillis() - start >= 10 * 1000) {
                    sendChat("Error! Server timed out on TabComplete packet!");
                    return;
                }
            }
            ArrayList<String> plugins = new ArrayList<>();
            tabCompletePacket.getMatches().forEach(tabCompleteMatch -> {
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
            setTabCompletePacket(null);
        }).start();
    }

    public static void setTabCompletePacket(ClientBoundTabCompletePacket clientBoundTabCompletePacket) {
        tabCompletePacket = clientBoundTabCompletePacket;
    }
}
