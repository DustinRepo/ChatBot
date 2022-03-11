package me.dustin.chatbot.command.impl;

import me.dustin.chatbot.command.Command;

import java.util.Random;
import java.util.UUID;

public class CommandRoulette extends Command {
    public CommandRoulette() {
        super("roulette");
        getAlias().add("russianroulette");
    }

    @Override
    public void run(String str, UUID sender) {
        Random r = new Random();
        if (r.nextInt(7) == 6)
            sendChat("*BANG!* You fuckin died");
        else
            sendChat("*click* You survive.. This time.");
    }
}
