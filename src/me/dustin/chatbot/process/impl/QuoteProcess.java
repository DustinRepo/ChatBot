package me.dustin.chatbot.process.impl;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import me.dustin.chatbot.event.EventReceiveChatMessage;
import me.dustin.chatbot.helper.GeneralHelper;
import me.dustin.chatbot.helper.MCAPIHelper;
import me.dustin.chatbot.helper.Timer;
import me.dustin.chatbot.network.ClientConnection;
import me.dustin.chatbot.network.packet.s2c.play.ClientBoundChatMessagePacket;
import me.dustin.chatbot.process.ChatBotProcess;
import me.dustin.events.core.EventListener;
import me.dustin.events.core.annotate.EventPointer;
import me.dustin.events.core.priority.Priority;

import java.io.File;
import java.util.*;

public class QuoteProcess extends ChatBotProcess {
    private final File file;
    private final Timer saveFileTimer = new Timer();
    public Map<String, ArrayList<String>> quotes = new HashMap<>();
    public QuoteProcess(ClientConnection clientConnection) {
        super(clientConnection);
        String ipString = clientConnection.getIp() + (clientConnection.getPort() == 25565 ? "" : ":" + clientConnection.getPort());
        File parentFolder = new File(new File("").getAbsolutePath() + File.separator + "trackers", ipString);
        if (!parentFolder.exists())
            parentFolder.mkdirs();
        this.file = new File(parentFolder, "quotes.json");
    }

    @Override
    public void init() {
        readFile();
        getClientConnection().getEventManager().register(this);
    }

    @EventPointer
    private final EventListener<EventReceiveChatMessage> eventReceiveChatMessageEventListener = new EventListener<>(event -> {
        if (event.getChatMessagePacket().getType() != ClientBoundChatMessagePacket.MESSAGE_TYPE_CHAT || event.getChatMessagePacket().getSender() == null)
            return;
        String uuid = event.getChatMessagePacket().getSender().toString().replace("-", "");
        if (GeneralHelper.matchUUIDs(uuid, getClientConnection().getSession().getUuid()))
            return;
        String body = GeneralHelper.strip(event.getChatMessagePacket().getMessage().getBody());
        handleCommand(body);
        if (quotes.containsKey(uuid))
            quotes.get(uuid).add(body);
        else {
            ArrayList<String> arrayList = new ArrayList<>();
            arrayList.add(body);
            quotes.put(uuid, arrayList);
        }
    }, Priority.LAST);

    public void handleCommand(String str) {
        if (str.startsWith("!quote ")) {
            if (str.split(" ").length == 1) {
                sendChat("Error! You have to specify a player name!");
                return;
            }
            String name = str.split(" ")[1];
            UUID uuid = MCAPIHelper.getUUIDFromName(name);
            if (uuid == null) {
                sendChat("Error! Could not get UUID from name!");
                return;
            }
            if (name.equalsIgnoreCase(getClientConnection().getSession().getUsername())) {
                sendChat("I do not track myself in this stat.");
                return;
            }
            String id = uuid.toString().replace("-", "");
            if (GeneralHelper.matchUUIDs(id, getClientConnection().getSession().getUuid()))
                return;
            ArrayList<String> quotes = this.quotes.get(id);
            if (quotes == null || quotes.isEmpty()) {
                sendChat("I don't have any quotes from " + name + " yet");
                return;
            }
            Random random = new Random();
            String quote = quotes.get(random.nextInt(quotes.size()));
            sendChat("<" + name + "> " + quote);
        } else if (str.equalsIgnoreCase("!quote")) {
            sendChat("Error! You have to specify a player name!");
            return;
        }
    }

    @Override
    public void tick() {
        if (saveFileTimer.hasPassed(30 * 1000L)) {
            saveFile();
            saveFileTimer.reset();
        }
    }

    @Override
    public void stop() {
        saveFile();
        getClientConnection().getEventManager().unregister(this);
    }

    public void readFile() {
        if (!file.exists())
            return;
        String s = GeneralHelper.readFile(file);
        JsonArray jsonArray = GeneralHelper.prettyGson.fromJson(s, JsonArray.class);
        for (int i = 0; i < jsonArray.size(); i++) {
            JsonObject jsonObject = jsonArray.get(i).getAsJsonObject();
            String uuid = jsonObject.get("uuid").getAsString();
            JsonArray quotesArray = jsonObject.get("quotes").getAsJsonArray();
            ArrayList<String> list = new ArrayList<>();
            for (int ii = 0; ii < quotesArray.size(); ii++) {
                list.add(quotesArray.get(ii).getAsString());
            }
            if (quotes.containsKey(uuid))
                quotes.get(uuid).addAll(list);
            else
                quotes.put(uuid, list);
        }
    }

    public void saveFile() {
        JsonArray jsonArray = new JsonArray();

        quotes.forEach((s, i) -> {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("uuid", s);
            JsonArray quotesArray = new JsonArray();
            for (String s1 : i) {
                quotesArray.add(s1);
            }
            jsonObject.add("quotes", quotesArray);
            jsonArray.add(jsonObject);
        });
        ArrayList<String> list = new ArrayList<>(List.of(GeneralHelper.prettyGson.toJson(jsonArray).split("\n")));
        GeneralHelper.writeFile(file, list);
    }
}
