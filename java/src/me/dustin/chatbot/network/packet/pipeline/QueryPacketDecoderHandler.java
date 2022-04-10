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
import me.dustin.chatbot.network.packet.s2c.query.ClientBoundQueryResponsePacket;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QueryPacketDecoderHandler extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        if (!ctx.channel().isOpen())
            return;
        int i = in.readableBytes();
        if (i != 0) {
            PacketByteBuf packetByteBuf = new PacketByteBuf(in);
            int packetId = packetByteBuf.readVarInt();
            if (packetId == 0x00) {
                ClientBoundQueryResponsePacket packet = new ClientBoundQueryResponsePacket(ChatBot.getClientConnection() == null ? null : ChatBot.getClientConnection().getClientBoundPacketHandler());
                packet.createPacket(packetByteBuf);
                out.add(packet);
            }
            in.clear();
        }
    }
}
