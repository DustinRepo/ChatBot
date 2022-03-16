package me.dustin.chatbot.command;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import me.dustin.chatbot.ChatBot;
import me.dustin.chatbot.event.EventReceiveChatMessage;
import me.dustin.chatbot.helper.ClassHelper;
import me.dustin.chatbot.helper.GeneralHelper;
import me.dustin.chatbot.helper.StopWatch;
import me.dustin.chatbot.network.ClientConnection;
import me.dustin.chatbot.network.packet.s2c.play.ClientBoundChatMessagePacket;
import me.dustin.events.core.EventListener;
import me.dustin.events.core.annotate.EventPointer;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class CommandManager {

    private final ArrayList<Command> commands = new ArrayList<>();
    private final ArrayList<CustomCommand> customCommands = new ArrayList<>();
    private final ClientConnection clientConnection;

    public CommandManager(ClientConnection clientConnection) {
        this.clientConnection = clientConnection;
    }

    public void init() {
        getClientConnection().getEventManager().unregister(this);
        getClientConnection().getEventManager().register(this);
        commands.clear();
        customCommands.clear();
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
        File customCommandsFile = new File(new File("").getAbsolutePath(), "custom.json");
        if (customCommandsFile.exists()) {
            JsonArray array = GeneralHelper.gson.fromJson(GeneralHelper.readFile(customCommandsFile), JsonArray.class);
            for (int i = 0; i < array.size(); i++) {
                JsonObject obj = array.get(i).getAsJsonObject();
                JsonArray responseArray = obj.get("responses").getAsJsonArray();
                ArrayList<String> responses = new ArrayList<>();
                for (int ii = 0; ii < responseArray.size(); ii++) {
                    responses.add(responseArray.get(ii).getAsString());
                }
                String name = obj.get("name").getAsString();
                customCommands.add(new CustomCommand(name, responses));
            }
        }
    }

    @EventPointer
    private final EventListener<EventReceiveChatMessage> eventReceiveChatMessageEventListener = new EventListener<>(event -> {
        ClientBoundChatMessagePacket clientBoundChatMessagePacket = event.getChatMessagePacket();
        UUID sender = clientBoundChatMessagePacket.getSender();
        String string = GeneralHelper.strip(event.getChatMessagePacket().getMessage().getBody());
        String[] sA = string.split(" ");
        if (sA.length > 2 && sA[1].equalsIgnoreCase("whispers:")) {
            string = string.substring(sA[0].length() + sA[1].length() + 2);
            if (sender == null && getClientConnection().getPlayerManager().get(sA[1]) != null) {
                sender = getClientConnection().getPlayerManager().get(sA[1]).getUuid();
            }
        }
        if (!string.startsWith(ChatBot.getConfig().getCommandPrefix()) || (sender != null && GeneralHelper.matchUUIDs(sender.toString(), getClientConnection().getSession().getUuid()))) {
            return;
        }
        try {
            String cmd = string.split(" ")[0].replace(ChatBot.getConfig().getCommandPrefix(), "");
            String input;
            if (string.contains(" "))
                input = string.replace(cmd + " ", "");
            else
                input = string.replace(cmd, "");
            input = input.substring(1);
            for (Command command : commands) {
                if (command.getName().equalsIgnoreCase(cmd) || command.getAlias().contains(cmd.toLowerCase())) {
                    try {
                        command.run(input, sender);
                        return;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            for (CustomCommand customCommand : customCommands) {
                if (customCommand.getName().equalsIgnoreCase(cmd)) {
                    customCommand.runCommand();
                    return;
                }
            }
        } catch (IndexOutOfBoundsException e) {}
    });

    public ArrayList<Command> getCommands() {
        return commands;
    }

    public ClientConnection getClientConnection() {
        return clientConnection;
    }

    public class CustomCommand {
        private final String name;
        private final ArrayList<String> responses;
        public CustomCommand(String name, ArrayList<String> responses) {
            this.name = name;
            this.responses = responses;
        }

        public String getName() {
            return name;
        }

        public ArrayList<String> getResponses() {
            return responses;
        }

        public void runCommand() {
            Random random = new Random();
            int select = random.nextInt(responses.size());
            getClientConnection().getClientPlayer().chat(responses.get(select));
        }
    }
}
