package me.dustin.chatbot.command.impl;

import me.dustin.chatbot.command.Command;
import me.dustin.chatbot.network.packet.c2s.play.ServerBoundChatPacket;

import java.util.Random;

public class CommandCum extends Command {

    public CommandCum() {
        super("cum");
    }

    String[] cumMessages = new String[]{"Oh fuck im gonna cum! AAAAAAAAAAAAAAAAAAAAAAAAAHHHHHHHHHHHHHHHHHHHHHHH", "*nuts in your shoes* uwu did I do that?", "oh fuck don't stop I'm so close", "I'm about to bust a load so fat I'm gonna drown in it", "oh fuck, now I'm all sticky"};

    @Override
    public void run(String str) {
        int size = cumMessages.length - 1;
        Random random = new Random();
        int select = random.nextInt(size);
        sendChat(cumMessages[select]);
    }
}
