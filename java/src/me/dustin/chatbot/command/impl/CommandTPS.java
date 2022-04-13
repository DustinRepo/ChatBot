package me.dustin.chatbot.command.impl;

import me.dustin.chatbot.ChatBot;
import me.dustin.chatbot.command.Command;

import java.util.UUID;

public class CommandTPS extends Command {
    public CommandTPS() {
        super("tps");
    }

    @Override
    public void run(String str, UUID sender) {
        double instant = getClientConnection().getTpsHelper().getTPS(2);
        String s = String.format("TPS: Instant: %.2f", instant);
        if (getClientConnection().getTpsHelper().reportSize() >= 15) {
            double sec15 = getClientConnection().getTpsHelper().getTPS(15);
            s += String.format(" | 15 seconds: %.2f", sec15);
        }
        if (getClientConnection().getTpsHelper().reportSize() >= 30) {
            double sec30 = getClientConnection().getTpsHelper().getTPS(30);
            s += String.format(" | 30 seconds: %.2f", sec30);
        }
        if (getClientConnection().getTpsHelper().reportSize() >= 60) {
            double sec60 = getClientConnection().getTpsHelper().getTPS(60);
            s += String.format(" | 60 seconds: %.2f", sec60);
        }
        sendChat(s, sender);
    }
}
