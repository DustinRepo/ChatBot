package me.dustin.chatbot.network.packet.impl.login.c2s;

import me.dustin.chatbot.helper.KeyHelper;
import me.dustin.chatbot.nbt.NbtCompound;
import me.dustin.chatbot.nbt.NbtString;
import me.dustin.chatbot.network.ProtocolHandler;
import me.dustin.chatbot.network.key.PublicKeyContainer;
import me.dustin.chatbot.network.packet.Packet;
import me.dustin.chatbot.network.packet.pipeline.PacketByteBuf;

import java.io.IOException;
import java.util.Base64;
import java.util.Optional;

public class ServerBoundLoginStartPacket extends Packet {
    private final String name;
    private final Optional<PublicKeyContainer> keyContainer;
    public ServerBoundLoginStartPacket(String name, Optional<PublicKeyContainer> publicKeyContainer) {
        super(0x00);
        this.name = name;
        this.keyContainer = publicKeyContainer;
    }

    @Override
    public void createPacket(PacketByteBuf packetByteBuf) throws IOException {
        packetByteBuf.writeString(name);
        if (ProtocolHandler.getCurrent().getProtocolVer() > ProtocolHandler.getVersionFromName("1.18.2").getProtocolVer())
            packetByteBuf.writeOptional(keyContainer, (packetByteBuf1, publicKeyContainer) -> {
                packetByteBuf1.writeLong(publicKeyContainer.expiresAt().toEpochMilli());
                packetByteBuf1.writeByteArray(KeyHelper.getPublicKey(publicKeyContainer.keyString()).getEncoded());
                packetByteBuf1.writeByteArray(Base64.getDecoder().decode(publicKeyContainer.signature()));
            });
    }
}
