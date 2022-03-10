package me.dustin.chatbot.command.impl;

import me.dustin.chatbot.command.Command;

import java.util.Random;

public class Command8Ball extends Command {
    public Command8Ball() {
        super("8ball");
    }

    String[] responses = new String[]{"Yes, 100%", "No", "Maybe", "I don't know", "Ask again later", "Stop asking questions"};

    @Override
    public void run(String str) {
        if (str.isEmpty()) {
            sendChat("You can't ask an 8ball question without a question!");
        }
        int size = responses.length;
        Random random = new Random();
        int select = random.nextInt(size);
        sendChat(responses[select]);
    }
}
