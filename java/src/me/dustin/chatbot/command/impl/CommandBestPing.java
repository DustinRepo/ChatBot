package me.dustin.chatbot.command.impl;

import me.dustin.chatbot.command.Command;
import me.dustin.chatbot.entity.player.PlayerInfo;

import java.util.UUID;

public class CommandBestPing extends Command {
    public CommandBestPing() {
        super("bestping");
        getAlias().add("bp");
    }

    @Override
    public void run(String str, UUID sender) {
        if (getClientConnection().getPlayerManager().getPlayers().isEmpty()) {
            sendChat("Error! I don't see any players :(", sender);
            return;
        }
        int best = 9999;
        PlayerInfo player = null;

        for (PlayerInfo instancePlayer : getClientConnection().getPlayerManager().getPlayers()) {
            if (player == null || (instancePlayer.getPing() < best && instancePlayer.getPing() > 0)) {
                player = instancePlayer;
                best = player.getPing();
            }
        }
        sendChat(player.getName() + " has the lowest ping at " + best + "ms", sender);
    }
}
