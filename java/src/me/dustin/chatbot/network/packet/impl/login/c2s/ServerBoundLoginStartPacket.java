package me.dustin.chatbot.network.packet.impl.login.c2s;

import me.dustin.chatbot.nbt.NbtCompound;
import me.dustin.chatbot.nbt.NbtString;
import me.dustin.chatbot.network.ProtocolHandler;
import me.dustin.chatbot.network.key.PublicKeyContainer;
import me.dustin.chatbot.network.packet.Packet;
import me.dustin.chatbot.network.packet.pipeline.PacketByteBuf;

import java.io.IOException;

public class ServerBoundLoginStartPacket extends Packet {
    private final String name;
    private final NbtCompound publicKeyNbt;
    public ServerBoundLoginStartPacket(String name, PublicKeyContainer publicKeyContainer) {
        super(0x00);
        this.name = name;
        if (publicKeyContainer != null) {
            publicKeyNbt = new NbtCompound();
            publicKeyNbt.put("expires_at", new NbtString(publicKeyContainer.expiresAt().toString()));
            publicKeyNbt.put("key", new NbtString(publicKeyContainer.keyString()));
            publicKeyNbt.put("signature", new NbtString(publicKeyContainer.signature()));
        } else {
            publicKeyNbt = null;
        }
    }

    @Override
    public void createPacket(PacketByteBuf packetByteBuf) throws IOException {
        packetByteBuf.writeString(name);
        if (ProtocolHandler.getCurrent().getProtocolVer() > ProtocolHandler.getVersionFromName("1.18.2").getProtocolVer())
            packetByteBuf.writeOptionalNBT(publicKeyNbt);
    }
}
