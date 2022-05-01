package me.dustin.chatbot.process.impl;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import me.dustin.chatbot.event.EventReceiveChatMessage;
import me.dustin.chatbot.helper.GeneralHelper;
import me.dustin.chatbot.helper.MCAPIHelper;
import me.dustin.chatbot.helper.StopWatch;
import me.dustin.chatbot.network.ClientConnection;
import me.dustin.chatbot.network.packet.impl.play.s2c.ClientBoundChatMessagePacket;
import me.dustin.chatbot.process.ChatBotProcess;
import me.dustin.events.core.EventListener;
import me.dustin.events.core.annotate.EventPointer;

import java.io.File;
import java.util.*;

public class Chat2b2tCountProcess extends ChatBotProcess {
    private final File file;
    private final StopWatch saveFileStopWatch = new StopWatch();

    private final Map<String, Integer> counts = new HashMap<>();
    public Chat2b2tCountProcess(ClientConnection clientConnection) {
        super(clientConnection);
        File parentFolder = new File(new File("").getAbsolutePath(), "trackers");
        if (!parentFolder.exists())
            parentFolder.mkdirs();
        this.file = new File(parentFolder, "2b2tCount.json");
    }

    @Override
    public void init() {
        readFile();
        getClientConnection().getEventManager().register(this);
    }

    @EventPointer
    private final EventListener<EventReceiveChatMessage> eventReceiveChatMessageEventListener = new EventListener<>(event -> {
        ClientBoundChatMessagePacket packet = event.getChatMessagePacket();
        if (packet.getSender().uuid() == null)
            return;
        String m = GeneralHelper.strip(packet.getMessage().getBody());
        if (handleCommand(m)) {
            event.cancel();
            return;
        }
        String uuid = packet.getSender().uuid().toString().replace("-","");
        if (GeneralHelper.matchUUIDs(uuid, getClientConnection().getSession().getUuid()))
            return;
        int matches = GeneralHelper.countMatches(m.toLowerCase(), "2b2t") + GeneralHelper.countMatches(m.toLowerCase(), "2builders2tools") + GeneralHelper.countMatches(m.toLowerCase(), "oldest anarchy server in minecraft");
        if (matches == 0)
            matches = GeneralHelper.countMatches(m.toLowerCase(), "2b");
        if (matches > 0) {
            if (counts.containsKey(uuid)) {
                counts.replace(uuid, counts.get(uuid) + matches);
            } else
                counts.put(uuid, matches);
        }
    });

    public boolean handleCommand(String str) {
        if (str.startsWith("!2b2t ") || str.startsWith("!2b2tcount ")) {
            if (str.split(" ").length == 1) {
                sendChat("Error! You have to specify a player name!");
                return true;
            }
            String name = str.split(" ")[1];
            UUID uuid = MCAPIHelper.getUUIDFromName(name);
            if (uuid == null) {
                sendChat("Error! Could not get UUID from name!");
                return true;
            }
            if (name.equalsIgnoreCase(getClientConnection().getSession().getUsername())) {
                sendChat("I do not track myself in this stat.");
                return true;
            }
            String id = uuid.toString().replace("-", "");
            if (GeneralHelper.matchUUIDs(id, getClientConnection().getSession().getUuid()))
                return true;
            int count = counts.getOrDefault(id, 0);
            if (count > 0)
                sendChat(name + " has mentioned 2b2t " + count + " times");
            else
                sendChat(name + " hasn't mentioned 2b2t yet");
            return true;
        } else if (str.equalsIgnoreCase("!2b2tcount")) {
            sendChat("Error! You have to specify a player name!");
            return true;
        }
        return false;
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
        getClientConnection().getEventManager().unregister(this);
        saveFile();
    }

    public void readFile() {
        if (!file.exists())
            return;
        String s = GeneralHelper.readFile(file);
        try {
            JsonArray jsonArray = GeneralHelper.prettyGson.fromJson(s, JsonArray.class);
            for (int i = 0; i < jsonArray.size(); i++) {
                JsonObject jsonObject = jsonArray.get(i).getAsJsonObject();
                String uuid = jsonObject.get("uuid").getAsString();
                int count = jsonObject.get("count").getAsInt();
                counts.put(uuid, count);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void saveFile() {
        JsonArray jsonArray = new JsonArray();

        counts.forEach((s, i) -> {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("uuid", s);
            jsonObject.addProperty("count", i);
            jsonArray.add(jsonObject);
        });
        ArrayList<String> list = new ArrayList<>(List.of(GeneralHelper.prettyGson.toJson(jsonArray).split("\n")));
        GeneralHelper.writeFile(file, list);
    }
}
