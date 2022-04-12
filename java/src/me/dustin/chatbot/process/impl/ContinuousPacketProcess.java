package me.dustin.chatbot.process.impl;

import me.dustin.chatbot.network.ClientConnection;
import me.dustin.chatbot.network.packet.ProtocolHandler;
import me.dustin.chatbot.network.packet.c2s.play.ServerBoundPlayerOnGroundPacket;
import me.dustin.chatbot.network.packet.c2s.play.ServerBoundPlayerPositionAndRotationPacket;
import me.dustin.chatbot.network.packet.c2s.play.ServerBoundPlayerPositionPacket;
import me.dustin.chatbot.network.player.ClientPlayer;
import me.dustin.chatbot.process.ChatBotProcess;

public class ContinuousPacketProcess extends ChatBotProcess {
    private int ticks = 0;
    public ContinuousPacketProcess(ClientConnection clientConnection) {
        super(clientConnection);
    }

    @Override
    public void init() {}

    @Override
    public void tick() {
        ClientPlayer clientPlayer = getClientConnection().getClientPlayer();
        boolean below1_9 = ProtocolHandler.getCurrent().getProtocolVer() < ProtocolHandler.getVersionFromName("1.9").getProtocolVer();
        if (below1_9) {
            if (ticks % 20 == 0) {
                getClientConnection().sendPacket(new ServerBoundPlayerPositionPacket(clientPlayer.getX(), clientPlayer.getY(), clientPlayer.getZ(), true));
            } else {
                getClientConnection().sendPacket(new ServerBoundPlayerOnGroundPacket(true));
            }
        } else if (ticks % 20 == 0) {
            getClientConnection().sendPacket(new ServerBoundPlayerPositionPacket(clientPlayer.getX(), clientPlayer.getY(), clientPlayer.getZ(), true));
        }
        ticks++;
    }

    @Override
    public void stop() {}
}
