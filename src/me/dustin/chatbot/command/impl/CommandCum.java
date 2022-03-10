package me.dustin.chatbot.command.impl;

import me.dustin.chatbot.command.Command;
import me.dustin.chatbot.network.player.OtherPlayer;

import java.util.Random;
import java.util.UUID;

public class CommandCum extends Command {

    public CommandCum() {
        super("cum");
    }

    String[] cumMessages = new String[]{"oh fuck {SENDER} you made me cum", "Oh fuck im gonna cum! AAAAAAAAAAAAAAAAAAAAAAAAAHHHHHHHHHHHHHHHHHHHHHHH", "*nuts in your shoes* uwu did I do that?", "oh fuck don't stop I'm so close", "I'm about to bust a load so fat I'm gonna drown in it", "oh fuck, now I'm all sticky"};

    @Override
    public void run(String str, UUID send) {
        OtherPlayer player = getClientConnection().getPlayerManager().get(send);
        int size = cumMessages.length;
        Random random = new Random();
        int select = random.nextInt(size);
        String message = cumMessages[select];
        String name = player == null ? "you" : player.getName();
        sendChat(message.replace("{SENDER}", name));
    }
}
