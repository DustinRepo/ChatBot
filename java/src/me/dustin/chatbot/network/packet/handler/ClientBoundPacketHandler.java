package me.dustin.chatbot.network.packet.handler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import me.dustin.chatbot.ChatBot;
import me.dustin.chatbot.helper.BadPacketException;
import me.dustin.chatbot.helper.GeneralHelper;
import me.dustin.chatbot.network.ClientConnection;
import me.dustin.chatbot.network.packet.Packet;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

public class ClientBoundPacketHandler extends SimpleChannelInboundHandler<Packet.ClientBoundPacket> {

    protected ChannelHandlerContext channelHandlerContext;
    protected Channel channel;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        this.getClientConnection().setChannel(ctx.channel());
        this.channelHandlerContext = ctx;
        this.channel = ctx.channel();
        super.channelActive(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        ChatBot.getClientConnection().close();
        super.channelInactive(ctx);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Packet.ClientBoundPacket packet) {
        if (channel.isOpen() && packet != null) {
            packet.apply();
        }
    }

    protected ClientConnection getClientConnection() {
        return ChatBot.getClientConnection();
    }

}
