package me.dustin.chatbot.process.impl;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import me.dustin.chatbot.ChatBot;
import me.dustin.chatbot.event.EventReceiveChatMessage;
import me.dustin.chatbot.helper.GeneralHelper;
import me.dustin.chatbot.helper.MCAPIHelper;
import me.dustin.chatbot.helper.StopWatch;
import me.dustin.chatbot.network.ClientConnection;
import me.dustin.chatbot.process.ChatBotProcess;
import me.dustin.events.core.EventListener;
import me.dustin.events.core.annotate.EventPointer;
import me.dustin.events.core.priority.Priority;

import java.io.File;
import java.util.*;

public class QuoteProcess extends ChatBotProcess {
    private static File file;
    private final StopWatch saveFileStopWatch = new StopWatch();
    public static Map<String, ArrayList<String>> quotes = new HashMap<>();
    public QuoteProcess(ClientConnection clientConnection) {
        super(clientConnection);
    }

    @Override
    public void init() {
        getClientConnection().getEventManager().register(this);
    }

    @EventPointer
    private final EventListener<EventReceiveChatMessage> eventReceiveChatMessageEventListener = new EventListener<>(event -> {
        if (event.getChatMessagePacket().getSender().uuid() == null)
            return;
        String uuid = event.getChatMessagePacket().getSender().uuid().toString().replace("-", "");
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
            if (quotes.isEmpty())
                return;
            ArrayList<String> uuids = new ArrayList<>(quotes.keySet());
            Random r = new Random();
            String uuid = uuids.get(r.nextInt(uuids.size()));
            ArrayList<String> qs = quotes.get(uuid);
            String quote = qs.get(r.nextInt(qs.size()));
            sendChat("<" + MCAPIHelper.getNameFromUUID(GeneralHelper.uuidFromStringNoDashes(uuid)) + "> " + quote);
        }
    }

    @Override
    public void tick() {
        if (saveFileStopWatch.hasPassed(30 * 1000L)) {
            saveFile();
            saveFileStopWatch.reset();
        }
    }

    @Override
    public void stop() {
        if (!quotes.isEmpty())
            saveFile();
        getClientConnection().getEventManager().unregister(this);
    }

    public static void readFile() {
        if (!getFile().exists())
            return;
        quotes.clear();
        String s = GeneralHelper.readFile(getFile());
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
        GeneralHelper.writeFile(getFile(), list);
    }

    private static File getFile() {
        if (file == null) {
            String ipString = ChatBot.getClientConnection().getMinecraftServerAddress().getIp() + (ChatBot.getClientConnection().getMinecraftServerAddress().getPort() == 25565 ? "" : ":" + ChatBot.getClientConnection().getMinecraftServerAddress().getPort());
            File parentFolder = new File(new File("").getAbsolutePath() + File.separator + "trackers", ipString);
            if (!parentFolder.exists())
                parentFolder.mkdirs();
            file = new File(parentFolder, "quotes.json");
        }
        return file;
    }
}
