package me.dustin.chatbot.network.packet.pipeline;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.ReadTimeoutException;
import me.dustin.chatbot.ChatBot;
import me.dustin.chatbot.chat.ChatMessage;
import me.dustin.chatbot.helper.GeneralHelper;

import java.net.UnknownHostException;

public class NetworkExceptionHandler extends ChannelDuplexHandler {
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        if (cause instanceof ReadTimeoutException) {
            GeneralHelper.print("Timed out.", ChatMessage.TextColors.DARK_RED);
            ChatBot.getClientConnection().close();
        } else if (cause instanceof UnknownHostException) {
            GeneralHelper.print("Unknown host.", ChatMessage.TextColors.DARK_RED);
            ChatBot.getClientConnection().close();
        } else if (cause != null) {
            //cause.printStackTrace();
            GeneralHelper.print(cause.toString(), ChatMessage.TextColors.RED);
        }
    }
}
