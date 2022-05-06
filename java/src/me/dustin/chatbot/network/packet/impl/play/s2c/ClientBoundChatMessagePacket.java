package me.dustin.chatbot.network.packet.impl.play.s2c;

import com.google.common.base.Strings;
import com.google.gson.JsonObject;
import me.dustin.chatbot.ChatBot;
import me.dustin.chatbot.chat.ChatMessage;
import me.dustin.chatbot.chat.ChatSender;
import me.dustin.chatbot.entity.player.PlayerInfo;
import me.dustin.chatbot.entity.player.PlayerInfoManager;
import me.dustin.chatbot.helper.GeneralHelper;
import me.dustin.chatbot.helper.MCAPIHelper;
import me.dustin.chatbot.network.ProtocolHandler;
import me.dustin.chatbot.network.key.PublicKeyContainer;
import me.dustin.chatbot.network.key.SaltAndSig;
import me.dustin.chatbot.network.packet.pipeline.PacketByteBuf;
import me.dustin.chatbot.network.packet.Packet;
import me.dustin.chatbot.network.packet.handler.PlayClientBoundPacketHandler;
import me.dustin.chatbot.network.packet.handler.ClientBoundPacketHandler;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class ClientBoundChatMessagePacket extends Packet.ClientBoundPacket {
    public final static int MESSAGE_TYPE_CHAT = 0, MESSAGE_TYPE_SYSTEM = 1, MESSAGE_TYPE_GAME_INFO = 2;
    private final ChatMessage message;
    private final byte type;
    private final ChatSender sender;
    private final Instant expiresAt;
    private final SaltAndSig saltAndSig;
    private final ChatMessage teamName;

    public ClientBoundChatMessagePacket(PacketByteBuf packetByteBuf) {
        super(packetByteBuf);
        UUID uuid = null;
        String name;
        this.message = ChatMessage.of(packetByteBuf.readString());
        name = this.message.getSenderName();
        if (ProtocolHandler.getCurrent().getProtocolVer() > ProtocolHandler.getVersionFromName("1.7.10").getProtocolVer())
            this.type = packetByteBuf.readByte();
        else
            this.type = MESSAGE_TYPE_CHAT;
        if (ProtocolHandler.getCurrent().getProtocolVer() > ProtocolHandler.getVersionFromName("1.15.1").getProtocolVer())
            uuid = packetByteBuf.readUuid();
        else if (!this.message.getSenderName().isEmpty()) {
            uuid = MCAPIHelper.getUUIDFromName(this.message.getSenderName());
        }
        if (uuid != null && uuid.toString().equalsIgnoreCase("00000000-0000-0000-0000-000000000000"))
            uuid = null;
        if (ProtocolHandler.getCurrent().getProtocolVer() > ProtocolHandler.getVersionFromName("1.18.2").getProtocolVer()) {
            name = ChatMessage.parse(GeneralHelper.gson.fromJson(packetByteBuf.readString(), JsonObject.class));
            this.message.setSenderName(name);
            boolean isTeam = packetByteBuf.readBoolean();
            if (isTeam)
                this.teamName = ChatMessage.of(packetByteBuf.readString());
            else
                teamName = null;
            this.expiresAt = Instant.ofEpochSecond(packetByteBuf.readLong());
            this.saltAndSig = SaltAndSig.from(packetByteBuf);
        } else {
            expiresAt = Instant.now();
            saltAndSig = new SaltAndSig(0L, new byte[0]);
            teamName = null;
        }
        this.sender = new ChatSender(uuid, name);
    }

    @Override
    public void handlePacket(ClientBoundPacketHandler clientBoundPacketHandler) {
        ((PlayClientBoundPacketHandler)clientBoundPacketHandler).handleChatMessagePacket(this);
    }

    public boolean isAfter(Instant sendingTime) {
        return sendingTime.isAfter(this.expiresAt.plus(Duration.ofMinutes(4L)));
    }

    public ChatMessage getMessage() {
        return message;
    }

    public byte getType() {
        return type;
    }

    public ChatSender getSender() {
        return sender;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }

    public SaltAndSig getSaltAndSig() {
        return saltAndSig;
    }
}
