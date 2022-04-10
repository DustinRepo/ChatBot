package me.dustin.chatbot.network.packet.pipeline;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import me.dustin.chatbot.network.packet.Packet;


public class PacketEncoder extends MessageToByteEncoder<Packet> {
    @Override
    protected void encode(ChannelHandlerContext ctx, Packet packet, ByteBuf out) throws Exception {
        PacketByteBuf packetByteBuf = new PacketByteBuf(out);
        packetByteBuf.writeVarInt(packet.getPacketId());
        packet.createPacket(packetByteBuf);
    }
}
