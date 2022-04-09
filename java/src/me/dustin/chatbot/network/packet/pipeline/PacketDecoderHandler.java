package me.dustin.chatbot.network.packet.pipeline;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import me.dustin.chatbot.ChatBot;
import me.dustin.chatbot.network.ClientConnection;
import me.dustin.chatbot.network.packet.Packet;
import me.dustin.chatbot.network.packet.PacketIDs;
import me.dustin.chatbot.network.packet.handler.ClientBoundPacketHandler;
import me.dustin.chatbot.network.packet.s2c.login.*;
import me.dustin.chatbot.network.packet.s2c.play.*;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PacketDecoderHandler extends ByteToMessageDecoder {

    private final Map<Integer, Class<? extends Packet.ClientBoundPacket>> loginMap = new HashMap<>();
    private final Map<Integer, Class<? extends Packet.ClientBoundPacket>> playMap = new HashMap<>();

    public PacketDecoderHandler() {
        loginMap.put(0x00, ClientBoundDisconnectPacket.class);
        loginMap.put(0x01, ClientBoundEncryptionStartPacket.class);
        loginMap.put(0x02, ClientBoundLoginSuccessPacket.class);
        loginMap.put(0x03, ClientBoundSetCompressionPacket.class);
        loginMap.put(0x04, ClientBoundPluginRequestPacket.class);
        
        playMap.put(PacketIDs.ClientBound.CHAT_MESSAGE.getPacketId(), ClientBoundChatMessagePacket.class);
        playMap.put(PacketIDs.ClientBound.COMBAT_EVENT.getPacketId(), ClientBoundCombatEventPacket.class);
        playMap.put(PacketIDs.ClientBound.DISCONNECT.getPacketId(), ClientBoundDisconnectPlayPacket.class);
        playMap.put(PacketIDs.ClientBound.KEEP_ALIVE.getPacketId(), ClientBoundKeepAlivePacket.class);
        playMap.put(PacketIDs.ClientBound.PLAYER_INFO.getPacketId(), ClientBoundPlayerInfoPacket.class);
        playMap.put(PacketIDs.ClientBound.PLAYER_POS_LOOK.getPacketId(), ClientBoundPlayerPositionAndLookPacket.class);
        playMap.put(PacketIDs.ClientBound.RESOURCE_PACK_SEND.getPacketId(), ClientBoundResourcePackSendPacket.class);
        playMap.put(PacketIDs.ClientBound.TAB_COMPLETE.getPacketId(), ClientBoundTabCompletePacket.class);
        playMap.put(PacketIDs.ClientBound.UPDATE_HEALTH.getPacketId(), ClientBoundUpdateHealthPacket.class);
        playMap.put(PacketIDs.ClientBound.WORLD_TIME.getPacketId(), ClientBoundWorldTimePacket.class);
        playMap.put(PacketIDs.ClientBound.JOIN_GAME.getPacketId(), ClientBoundJoinGamePacket.class);
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        if (!ctx.channel().isOpen())
            return;
        int i = in.readableBytes();
        if (i != 0) {
            PacketByteBuf packetByteBuf = new PacketByteBuf(in);
            int packetId = packetByteBuf.readVarInt();
            Class<? extends Packet.ClientBoundPacket> c = ChatBot.getClientConnection().getNetworkState() == ClientConnection.NetworkState.PLAY ? playMap.get(packetId) : loginMap.get(packetId);
            if (c != null) {
                Packet.ClientBoundPacket packet = c.getDeclaredConstructor(ClientBoundPacketHandler.class).newInstance(ChatBot.getClientConnection().getClientBoundPacketHandler());
                packet.createPacket(packetByteBuf);
                out.add(packet);
            } else {
                in.clear();
            }
        }
    }
}
