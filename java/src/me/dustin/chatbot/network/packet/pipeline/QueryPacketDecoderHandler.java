package me.dustin.chatbot.network.packet.pipeline;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import me.dustin.chatbot.network.packet.impl.query.s2c.ClientBoundQueryResponsePacket;

import java.util.List;

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
                ClientBoundQueryResponsePacket packet = new ClientBoundQueryResponsePacket(packetByteBuf);
                packet.createPacket(packetByteBuf);
                out.add(packet);
            }
            in.clear();
        }
    }
}
