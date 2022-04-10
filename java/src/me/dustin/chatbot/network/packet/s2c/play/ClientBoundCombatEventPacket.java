package me.dustin.chatbot.network.packet.s2c.play;

import me.dustin.chatbot.ChatBot;
import me.dustin.chatbot.network.Protocols;
import me.dustin.chatbot.network.packet.PacketIDs;
import me.dustin.chatbot.network.packet.pipeline.PacketByteBuf;
import me.dustin.chatbot.network.packet.Packet;
import me.dustin.chatbot.network.packet.handler.PlayClientBoundPacketHandler;
import me.dustin.chatbot.network.packet.handler.ClientBoundPacketHandler;

import java.io.IOException;

public class ClientBoundCombatEventPacket extends Packet.ClientBoundPacket {
    public static final int ENTER_COMBAT = 0, END_COMBAT = 1, ENTITY_DIED = 2;
    private int type = -1;//1.16.5 and below

    private int playerId;
    private int killerId;
    private String message = "";
    public ClientBoundCombatEventPacket(ClientBoundPacketHandler clientBoundPacketHandler) {
        super(PacketIDs.ClientBound.COMBAT_EVENT.getPacketId(), clientBoundPacketHandler);
    }

    @Override
    public void createPacket(PacketByteBuf packetByteBuf) throws IOException {
        if (Protocols.getCurrent().getProtocolVer() <= Protocols.V1_16_5.getProtocolVer()) {//1.16.5
            this.type = packetByteBuf.readVarInt();
            if (type != ENTITY_DIED)
                return;
        }
        this.playerId = packetByteBuf.readVarInt();
        this.killerId = packetByteBuf.readInt();
        if (Protocols.getCurrent().getProtocolVer() >= Protocols.V1_17.getProtocolVer() || type == ENTITY_DIED)//1.17 or the player actually died
            this.message = packetByteBuf.readString();
    }

    @Override
    public void apply() {
        ((PlayClientBoundPacketHandler)clientBoundPacketHandler).handlePlayerDeadPacket(this);
    }

    public int getType() {
        return type;
    }

    public int getPlayerId() {
        return playerId;
    }

    public int getKillerId() {
        return killerId;
    }

    public String getMessage() {
        return message;
    }
}
