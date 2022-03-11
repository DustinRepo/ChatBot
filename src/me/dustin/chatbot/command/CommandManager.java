package me.dustin.chatbot.command;

import me.dustin.chatbot.ChatBot;
import me.dustin.chatbot.helper.ClassHelper;
import me.dustin.chatbot.network.ClientConnection;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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

    public boolean parse(String string, UUID sender) {
        if (!string.startsWith(ChatBot.getConfig().getCommandPrefix()) || sender.toString().equalsIgnoreCase(getClientConnection().getSession().getUuid())) {
            return false;
        }
        try {
            String cmd = string.split(" ")[0].replace(ChatBot.getConfig().getCommandPrefix(), "");
            String input;
            if (string.contains(" "))
                input = string.replace(cmd + " ", "");
            else
                input = string.replace(cmd, "");
            input = input.substring(1);
            System.out.println(input);
            for (Command command : commands) {
                if (command.getName().equalsIgnoreCase(cmd) || command.getAlias().contains(cmd.toLowerCase())) {
                    try {
                        command.run(input, sender);
                        return true;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (IndexOutOfBoundsException e) {}
        return false;
    }

    public ArrayList<Command> getCommands() {
        return commands;
    }

    public ClientConnection getClientConnection() {
        return clientConnection;
    }
}
