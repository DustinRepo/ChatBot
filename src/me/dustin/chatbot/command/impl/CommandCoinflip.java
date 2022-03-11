package me.dustin.chatbot.command.impl;

import me.dustin.chatbot.command.Command;

import java.util.Random;
import java.util.UUID;

public class CommandCoinflip extends Command {
    public CommandCoinflip() {
        super("coinflip");
    }

    @Override
    public void run(String str, UUID sender) {
        Random r = new Random();
        sendChat(r.nextBoolean() ? "Heads" : "Tails");
    }
}
