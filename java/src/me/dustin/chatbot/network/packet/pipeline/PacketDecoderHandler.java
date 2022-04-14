package me.dustin.chatbot.network.packet.pipeline;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import me.dustin.chatbot.ChatBot;
import me.dustin.chatbot.network.ClientConnection;
import me.dustin.chatbot.network.packet.Packet;
import me.dustin.chatbot.network.packet.ProtocolHandler;
import me.dustin.chatbot.network.packet.impl.login.s2c.*;
import me.dustin.chatbot.network.packet.impl.play.s2c.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PacketDecoderHandler extends ByteToMessageDecoder {

    private final Map<Integer, Class<? extends Packet.ClientBoundPacket>> loginMap = new HashMap<>();
    private final Map<Integer, Class<? extends Packet.ClientBoundPacket>> playMap = new HashMap<>();
    public PacketDecoderHandler() {
        loginMap.put(0x00, ClientBoundDisconnectLoginPacket.class);
        loginMap.put(0x01, ClientBoundEncryptionStartPacket.class);
        loginMap.put(0x02, ClientBoundLoginSuccessPacket.class);
        loginMap.put(0x03, ClientBoundSetCompressionPacket.class);
        loginMap.put(0x04, ClientBoundPluginRequestPacket.class);
        
        playMap.put(ProtocolHandler.getCurrent().getPacketId(ProtocolHandler.NetworkSide.CLIENTBOUND, "chat_message"), ClientBoundChatMessagePacket.class);
        playMap.put(ProtocolHandler.getCurrent().getPacketId(ProtocolHandler.NetworkSide.CLIENTBOUND, "kick"), ClientBoundDisconnectPlayPacket.class);
        playMap.put(ProtocolHandler.getCurrent().getPacketId(ProtocolHandler.NetworkSide.CLIENTBOUND, "heartbeat"), ClientBoundKeepAlivePacket.class);
        playMap.put(ProtocolHandler.getCurrent().getPacketId(ProtocolHandler.NetworkSide.CLIENTBOUND, "tab_list", "legacy_tab_list"), ClientBoundPlayerInfoPacket.class);
        playMap.put(ProtocolHandler.getCurrent().getPacketId(ProtocolHandler.NetworkSide.CLIENTBOUND, "position_rotation"), ClientBoundPlayerPositionAndLookPacket.class);
        playMap.put(ProtocolHandler.getCurrent().getPacketId(ProtocolHandler.NetworkSide.CLIENTBOUND, "resourcepack"), ClientBoundResourcePackSendPacket.class);
        playMap.put(ProtocolHandler.getCurrent().getPacketId(ProtocolHandler.NetworkSide.CLIENTBOUND, "chat_suggestions"), ClientBoundTabCompletePacket.class);
        playMap.put(ProtocolHandler.getCurrent().getPacketId(ProtocolHandler.NetworkSide.CLIENTBOUND, "health"), ClientBoundUpdateHealthPacket.class);
        playMap.put(ProtocolHandler.getCurrent().getPacketId(ProtocolHandler.NetworkSide.CLIENTBOUND, "time"), ClientBoundWorldTimePacket.class);
        playMap.put(ProtocolHandler.getCurrent().getPacketId(ProtocolHandler.NetworkSide.CLIENTBOUND, "initialize"), ClientBoundJoinGamePacket.class);
        playMap.put(ProtocolHandler.getCurrent().getPacketId(ProtocolHandler.NetworkSide.CLIENTBOUND, "ping"), ClientBoundPingPacket.class);
        playMap.put(ProtocolHandler.getCurrent().getPacketId(ProtocolHandler.NetworkSide.CLIENTBOUND, "difficulty"), ClientBoundServerDifficultyPacket.class);
        playMap.put(ProtocolHandler.getCurrent().getPacketId(ProtocolHandler.NetworkSide.CLIENTBOUND, "plugin"), ClientBoundCustomDataPacket.class);
        playMap.put(ProtocolHandler.getCurrent().getPacketId(ProtocolHandler.NetworkSide.CLIENTBOUND, "entity_destroy"), ClientBoundRemoveEntities.class);
        playMap.put(ProtocolHandler.getCurrent().getPacketId(ProtocolHandler.NetworkSide.CLIENTBOUND, "entity_player"), ClientBoundSpawnPlayerPacket.class);
        playMap.put(ProtocolHandler.getCurrent().getPacketId(ProtocolHandler.NetworkSide.CLIENTBOUND, "entity_mob_spawn"), ClientBoundSpawnMobPacket.class);
        playMap.put(ProtocolHandler.getCurrent().getPacketId(ProtocolHandler.NetworkSide.CLIENTBOUND, "teleport"), ClientBoundEntityTeleportPacket.class);
        playMap.put(ProtocolHandler.getCurrent().getPacketId(ProtocolHandler.NetworkSide.CLIENTBOUND, "relative_move"), ClientBoundEntityPositionPacket.class);
        playMap.put(ProtocolHandler.getCurrent().getPacketId(ProtocolHandler.NetworkSide.CLIENTBOUND, "movement_rotation"), ClientBoundEntityPositionPacket.class);
        playMap.put(ProtocolHandler.getCurrent().getPacketId(ProtocolHandler.NetworkSide.CLIENTBOUND, "velocity"), ClientBoundEntityVelocityPacket.class);
        //playMap.put(ProtocolHandler.getCurrent().getPacketId(ProtocolHandler.NetworkSide.CLIENTBOUND, "chunk"), ClientBoundChunkDataPacket.class);
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
                Packet.ClientBoundPacket packet = c.getDeclaredConstructor(PacketByteBuf.class).newInstance(packetByteBuf);
                out.add(packet);
            }
            in.clear();
        }
    }
}
