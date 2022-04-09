package me.dustin.chatbot.network.packet.pipeline;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class PacketSizePrepender extends MessageToByteEncoder<ByteBuf> {

    protected void encode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, ByteBuf byteBuf2) {
        int i = byteBuf.readableBytes();
        int j = PacketByteBuf.getVarIntLength(i);
        if (j > 3) {
            throw new IllegalArgumentException(i + " too large, expected <= 3");
        } else {
            PacketByteBuf packetByteBuf = new PacketByteBuf(byteBuf2);
            packetByteBuf.ensureWritable(j + i);
            packetByteBuf.writeVarInt(i);
            packetByteBuf.writeBytes(byteBuf, byteBuf.readerIndex(), i);
        }
    }
}