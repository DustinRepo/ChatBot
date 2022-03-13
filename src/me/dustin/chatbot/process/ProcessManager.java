package me.dustin.chatbot.process;

import me.dustin.chatbot.network.ClientConnection;

import java.util.ArrayList;

public class ProcessManager {

    private final ClientConnection clientConnection;
    private final ArrayList<ChatBotProcess> processes = new ArrayList<>();

    public ProcessManager(ClientConnection clientConnection) {
        this.clientConnection = clientConnection;
    }

    public void addProcess(ChatBotProcess chatBotProcess) {
        processes.add(chatBotProcess);
        chatBotProcess.init();
    }

    public void stop(ChatBotProcess chatBotProcess) {
        chatBotProcess.stop();
        processes.remove(chatBotProcess);
    }

    public void stopAll() {
        for (int i = 0; i < processes.size(); i++) {
            ChatBotProcess chatBotProcess = processes.get(i);
            chatBotProcess.stop();
            processes.remove(chatBotProcess);
        }
    }

    public void tick() {
        processes.forEach(ChatBotProcess::tick);
    }

    public ArrayList<ChatBotProcess> getProcesses() {
        return processes;
    }

    public ClientConnection getClientConnection() {
        return clientConnection;
    }
}
