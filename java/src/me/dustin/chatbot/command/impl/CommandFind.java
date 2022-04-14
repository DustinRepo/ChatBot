package me.dustin.chatbot.command.impl;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import me.dustin.chatbot.command.Command;
import me.dustin.chatbot.event.EventTick;
import me.dustin.chatbot.helper.GeneralHelper;
import me.dustin.chatbot.network.MinecraftServerAddress;
import me.dustin.chatbot.network.packet.ProtocolHandler;
import me.dustin.chatbot.network.packet.impl.handshake.ServerBoundHandshakePacket;
import me.dustin.events.core.EventListener;
import me.dustin.events.core.annotate.EventPointer;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.UUID;

public class CommandFind extends Command {
    private final ArrayList<String> servers = new ArrayList<>();
    private final ArrayList<Thread> threads = new ArrayList<>();
    private boolean isSearching;
    private boolean found;
    private String name;
    private UUID sender;
    public CommandFind() {
        super("find");
        servers.add("50kilo.org");
        servers.add("play.snapshotanarchy.net");
        servers.add("anarchycraft.minecraft.best");
        servers.add("hardcoreanarchy.gay");
        servers.add("0b0t.org");
        servers.add("pvp.meteorclient.com");
        servers.add("Specterium.com");
    }

    @Override
    public void run(String str, UUID sender) {
        if (isSearching) {
            sendChat("Already searching through servers!", sender);
            return;
        }
        if (str.isEmpty()) {
            sendChat("Error! You have to specify a play name!", sender);
            return;
        }
        this.sender = sender;
        String name = str.split(" ")[0];
        this.name = name;
        searchServers(name);
        getClientConnection().getEventManager().register(this);
    }

    @EventPointer
    private final EventListener<EventTick> eventTickEventListener = new EventListener<>(event -> {
        if (isSearching) {
            if (found) {
                threads.forEach(Thread::interrupt);
                threads.clear();
                found = false;
                isSearching = false;
                getClientConnection().getEventManager().unregister(this);
                return;
            }
            boolean runningThread = false;
            for (Thread thread : threads) {
                if (thread.isAlive() && !thread.isInterrupted()) {
                    runningThread = true;
                    break;
                }
            }
            if (!runningThread) {
                sendChat("Player " + name + " not found.", sender);
                isSearching = false;
                threads.clear();
                getClientConnection().getEventManager().unregister(this);
            }
        }
    });

    private void searchServers(String name) {
        isSearching = true;
        servers.forEach(s -> {
            Thread thread = new Thread(() -> {
                int port = 25565;
                String ip = s;
                if (ip.contains(":")) {
                    port = Integer.parseInt(s.split(":")[1]);
                    ip = ip.split(":")[0];
                }
                MinecraftServerAddress minecraftServerAddress = MinecraftServerAddress.resolve(ip, port);
                try {
                    Socket socket = new Socket(minecraftServerAddress.getIp(), minecraftServerAddress.getPort());
                    DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
                    DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());

                    sendHandshake(dataOutputStream, minecraftServerAddress);
                    sendQueryRequest(dataOutputStream);

                    int size = readVarInt(dataInputStream);//even tho we don't use it currently you MUST read this in order to not mess up the order of others being read
                    int packetID = readVarInt(dataInputStream);

                    if (packetID != 0x00) {
                        return;
                    }

                    String resp = receiveQueryRequest(dataInputStream);
                    JsonObject jsonObject = GeneralHelper.gson.fromJson(resp, JsonObject.class);
                    JsonObject playersObject = jsonObject.getAsJsonObject("players");
                    JsonArray jsonArray = playersObject.getAsJsonArray("sample");
                    if (jsonArray != null)
                        for (int i = 0; i < jsonArray.size(); i++) {
                            JsonObject playerObj = jsonArray.get(i).getAsJsonObject();
                            String playerName = playerObj.get("name").getAsString();
                            if (playerName.equalsIgnoreCase(name)) {//we found him
                                sendChat(name + " was found on: " + minecraftServerAddress.getIp() + ":" + minecraftServerAddress.getPort(), sender);
                                found = true;
                            }
                        }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            thread.start();
            threads.add(thread);
        });
    }

    private void sendHandshake(DataOutputStream dataOutputStream, MinecraftServerAddress serverAddress) {
        try {
            ByteArrayOutputStream handshakeBytes = new ByteArrayOutputStream();
            DataOutputStream handshakePacket = new DataOutputStream(handshakeBytes);
            handshakePacket.writeByte(0x00);//packet id
            writeVarInt(handshakePacket, ProtocolHandler.getCurrent().getProtocolVer());//protocol version
            writeVarInt(handshakePacket, serverAddress.getIp().length());//length of address
            handshakePacket.writeBytes(serverAddress.getIp());//address
            handshakePacket.writeShort(serverAddress.getPort());//port
            writeVarInt(handshakePacket, ServerBoundHandshakePacket.STATUS_STATE);//status id

            writeVarInt(dataOutputStream, handshakeBytes.size());//size of data
            dataOutputStream.write(handshakeBytes.toByteArray());//data
            handshakeBytes.close();
            handshakePacket.close();
        } catch (Exception e) {e.printStackTrace();}
    }

    private void sendQueryRequest(DataOutputStream dataOutputStream) {
        try {
            dataOutputStream.writeByte(0x01);//size of data
            dataOutputStream.writeByte(0x00);//packet id
        } catch (Exception e) {e.printStackTrace();}
    }

    private String receiveQueryRequest(DataInputStream dataInputStream) {
        try {
            int strLength = readVarInt(dataInputStream);
            byte[] strBytes = new byte[strLength];
            dataInputStream.readFully(strBytes);
            return new String(strBytes);
        } catch (Exception e) {e.printStackTrace();}
        return "null";
    }

    private void writeVarInt(DataOutputStream out, int paramInt) throws IOException {
        while (true) {
            if ((paramInt & 0xFFFFFF80) == 0) {
                out.write(paramInt);
                return;
            }

            out.write(paramInt & 0x7F | 0x80);
            paramInt >>>= 7;
        }
    }

    private int readVarInt(DataInputStream in) throws IOException {
        int i = 0;
        int j = 0;
        while (true) {
            int k = in.readByte();

            i |= (k & 0x7F) << j++ * 7;

            if (j > 5) {
                throw new RuntimeException("VarInt too big");
            }

            if ((k & 0x80) != 128) {
                break;
            }
        }

        return i;
    }
}
