package me.dustin.chatbot.network.packet.s2c.query;

import me.dustin.chatbot.network.packet.Packet;
import me.dustin.chatbot.network.packet.handler.ClientBoundPacketHandler;
import me.dustin.chatbot.network.packet.pipeline.PacketByteBuf;

import java.io.IOException;

public class ClientBoundQueryResponsePacket extends Packet.ClientBoundPacket {

    private String jsonData;

    public ClientBoundQueryResponsePacket(ClientBoundPacketHandler clientBoundPacketHandler) {
        super(clientBoundPacketHandler);
    }

    @Override
    public void createPacket(PacketByteBuf packetByteBuf) throws IOException {
        this.jsonData = packetByteBuf.readString(32767);
    }

    @Override
    public void apply() {

    }

    public String getJsonData() {
        return jsonData;
    }
}
