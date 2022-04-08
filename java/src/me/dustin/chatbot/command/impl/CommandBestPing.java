package me.dustin.chatbot.command.impl;

import me.dustin.chatbot.command.Command;
import me.dustin.chatbot.network.player.OtherPlayer;
import me.dustin.chatbot.network.player.PlayerManager;

import java.util.UUID;

public class CommandBestPing extends Command {
    public CommandBestPing() {
        super("bestping");
        getAlias().add("bp");
    }

    @Override
    public void run(String str, UUID send) {
        if (getClientConnection().getPlayerManager().getPlayers().isEmpty()) {
            sendChat("Error! I don't see any players :(");
            return;
        }
        int best = 9999;
        OtherPlayer player = null;

        for (OtherPlayer instancePlayer : getClientConnection().getPlayerManager().getPlayers()) {
            if (player == null || (instancePlayer.getPing() < best && instancePlayer.getPing() > 0)) {
                player = instancePlayer;
                best = player.getPing();
            }
        }
        sendChat(player.getName() + " has the lowest ping at " + best + "ms");
    }
}
