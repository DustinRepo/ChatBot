package me.dustin.chatbot.network.packet.pipeline;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.DecoderException;

import java.util.List;
import java.util.zip.Inflater;

public class PacketInflater extends ByteToMessageDecoder {
    private final Inflater inflater;
    private int compressionThreshold;

    public PacketInflater(int compressionThreshold) {
        this.compressionThreshold = compressionThreshold;
        this.inflater = new Inflater();
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        if (in.readableBytes() != 0) {
            PacketByteBuf packetByteBuf = new PacketByteBuf(in);
            int i = packetByteBuf.readVarInt();
            if (i == 0) {
                out.add(packetByteBuf.readBytes(packetByteBuf.readableBytes()));
            } else {
                if (i < this.compressionThreshold) {
                    throw new DecoderException("Badly compressed packet - size of " + i + " is below server threshold of " + this.compressionThreshold);
                }

                if (i > 8388608) {
                    throw new DecoderException("Badly compressed packet - size of " + i + " is larger than protocol maximum of 8388608");
                }

                byte[] bs = new byte[packetByteBuf.readableBytes()];
                packetByteBuf.readBytes(bs);
                this.inflater.setInput(bs);
                byte[] cs = new byte[i];
                this.inflater.inflate(cs);
                out.add(Unpooled.wrappedBuffer(cs));
                this.inflater.reset();
            }
        }
    }

    public void setCompressionThreshold(int compressionThreshold) {
        this.compressionThreshold = compressionThreshold;
    }
}
