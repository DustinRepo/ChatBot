package me.dustin.chatbot.chat;

public class ChatMessage {

    public String senderName;
    public String body;

    public ChatMessage(String senderName, String body) {
        this.senderName = senderName;
        this.body = body;
    }

    public String getMessage() {
        if (senderName.isEmpty())
            return body;
        return senderName + " " + body;
    }

    public String getSenderName() {
        return senderName;
    }

    public String getBody() {
        return body;
    }
}
