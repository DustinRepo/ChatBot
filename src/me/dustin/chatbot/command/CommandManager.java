package me.dustin.chatbot.command;

import me.dustin.chatbot.ChatBot;
import me.dustin.chatbot.helper.ClassHelper;
import me.dustin.chatbot.network.ClientConnection;

import java.util.ArrayList;
import java.util.List;

public class CommandManager {

    private final ArrayList<Command> commands = new ArrayList<>();
    private final ClientConnection clientConnection;

    public CommandManager(ClientConnection clientConnection) {
        this.clientConnection = clientConnection;
    }


    public void init() {
        commands.clear();
        List<Class<?>> classes = ClassHelper.INSTANCE.getClasses("me.dustin.chatbot.command.impl", Command.class);
        classes.forEach(clazz -> {
            try {
                @SuppressWarnings("deprecation")
                Command instance = (Command) clazz.newInstance();
                instance.setClientConnection(clientConnection);
                getCommands().add(instance);
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
        });
    }

    public boolean parse(String string) {
        if (!string.contains(ChatBot.getConfig().getCommandPrefix())) {
            return false;
        }
        try {
            String first = string.split(ChatBot.getConfig().getCommandPrefix())[1];
            String cmd = first.split(" ")[0];
            String input;
            if (first.contains(" "))
                input = first.replace(cmd + " ", "");
            else
                input = first.replace(cmd, "");

            for (Command command : commands) {
                if (command.getName().equalsIgnoreCase(cmd) || command.getAlias().contains(cmd.toLowerCase())) {
                    command.run(input);
                    return true;
                }
            }
        } catch (IndexOutOfBoundsException e) {}
        return false;
    }

    public ArrayList<Command> getCommands() {
        return commands;
    }
}
