package me.dustin.chatbot.command;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import me.dustin.chatbot.ChatBot;
import me.dustin.chatbot.event.EventReceiveChatMessage;
import me.dustin.chatbot.helper.ClassHelper;
import me.dustin.chatbot.helper.GeneralHelper;
import me.dustin.chatbot.helper.MCAPIHelper;
import me.dustin.chatbot.network.ClientConnection;
import me.dustin.chatbot.network.packet.impl.play.s2c.ClientBoundChatMessagePacket;
import me.dustin.events.core.EventListener;
import me.dustin.events.core.annotate.EventPointer;

import java.io.File;
import java.util.*;

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
        if (!ChatBot.getConfig().isCommands())
            return;
        List<Class<?>> classes = ClassHelper.INSTANCE.getClasses("me.dustin.chatbot.command.impl", Command.class);
        classes.forEach(clazz -> {
            try {
                @SuppressWarnings("deprecation")
                Command instance = (Command) clazz.newInstance();
                getCommands().add(instance);
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
        });
        File customCommandsFile = new File(new File("").getAbsolutePath() + File.separator + "custom", "custom.json");
        if (customCommandsFile.exists()) {
            JsonArray array = GeneralHelper.gson.fromJson(GeneralHelper.readFile(customCommandsFile), JsonArray.class);
            for (int i = 0; i < array.size(); i++) {
                JsonObject obj = array.get(i).getAsJsonObject();
                ArrayList<String> names = new ArrayList<>();
                JsonArray namesArray = obj.get("names").getAsJsonArray();
                for (int ii = 0; ii < namesArray.size(); ii++) {
                    names.add(namesArray.get(ii).getAsString());
                }
                JsonArray responseArray = obj.get("responses").getAsJsonArray();
                ArrayList<String> responses = new ArrayList<>();
                for (int ii = 0; ii < responseArray.size(); ii++) {
                    responses.add(responseArray.get(ii).getAsString());
                }
                customCommands.add(new CustomCommand(names, responses));
            }
        }
    }

    @EventPointer
    private final EventListener<EventReceiveChatMessage> eventReceiveChatMessageEventListener = new EventListener<>(event -> {
        boolean directMessage = false;
        ClientBoundChatMessagePacket clientBoundChatMessagePacket = event.getChatMessagePacket();
        UUID sender = clientBoundChatMessagePacket.getSender().uuid();
        String string = GeneralHelper.strip(event.getChatMessagePacket().getMessage().getBody());
        String[] sA = string.split(" ");
        if (sA.length > 2 && sA[1].equalsIgnoreCase("whispers:")) {
            string = string.substring(sA[0].length() + sA[1].length() + 2);
            if (sender == null && getClientConnection().getPlayerManager().get(sA[0]) != null) {
                sender = getClientConnection().getPlayerManager().get(sA[0]).getUuid();
                directMessage = true;
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
                        command.setDirectMessage(directMessage);
                        command.run(input, sender);
                        return;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            for (CustomCommand customCommand : customCommands) {
                if (customCommand.getNames().contains(cmd.toLowerCase())) {
                    customCommand.runCommand(input, sender, directMessage);
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
        private final ArrayList<String> names;
        private final ArrayList<String> responses;
        public CustomCommand(ArrayList<String> name, ArrayList<String> responses) {
            this.names = name;
            this.responses = responses;
        }

        public ArrayList<String> getNames() {
            return names;
        }

        public ArrayList<String> getResponses() {
            return responses;
        }

        public void runCommand(String input, UUID sender, boolean directMessage) {
            Random random = new Random();
            int select = random.nextInt(responses.size());
            String response = responses.get(select);
            if (sender != null) {
                response = response.replace("{SENDER_UUID}", sender.toString());
                response = response.replace("{SENDER_UUID_NO_DASH}", sender.toString().replace("-", ""));
                response = response.replace("{SENDER_NAME}", MCAPIHelper.getNameFromUUID(sender));
            }
            if (directMessage && !response.startsWith("/"))
                getClientConnection().getClientPlayer().chat("/msg " + getClientConnection().getPlayerManager().get(sender).getName() + " " + response);
            else
            getClientConnection().getClientPlayer().chat(response);
        }
    }
}
