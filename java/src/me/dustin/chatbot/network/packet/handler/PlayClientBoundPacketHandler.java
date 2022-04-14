package me.dustin.chatbot.network.packet.handler;

import io.netty.buffer.Unpooled;
import me.dustin.chatbot.chat.ChatMessage;
import me.dustin.chatbot.entity.LivingEntity;
import me.dustin.chatbot.entity.player.PlayerEntity;
import me.dustin.chatbot.event.EventAddPlayer;
import me.dustin.chatbot.event.EventReceiveChatMessage;
import me.dustin.chatbot.event.EventRemovePlayer;
import me.dustin.chatbot.helper.GeneralHelper;
import me.dustin.chatbot.network.packet.Packet;
import me.dustin.chatbot.network.packet.ProtocolHandler;
import me.dustin.chatbot.network.packet.impl.play.c2s.*;
import me.dustin.chatbot.network.packet.impl.play.s2c.*;
import me.dustin.chatbot.network.packet.pipeline.PacketByteBuf;
import me.dustin.chatbot.network.player.ClientPlayer;
import me.dustin.chatbot.entity.player.PlayerInfo;
import me.dustin.chatbot.world.World;

import java.util.UUID;

public class PlayClientBoundPacketHandler extends ClientBoundPacketHandler {

    public void handleDisconnectPacket(ClientBoundDisconnectPlayPacket clientBoundDisconnectPacket) {
        GeneralHelper.print("Disconnected", ChatMessage.TextColor.DARK_RED);
        GeneralHelper.printChat(ChatMessage.of(clientBoundDisconnectPacket.getReason()));
    }

    public void handleKeepAlivePacket(ClientBoundKeepAlivePacket keepAlivePacket) {
        getClientConnection().sendPacket(new ServerBoundKeepAlivePacket(keepAlivePacket.getId()));
    }

    public void handleChatMessagePacket(ClientBoundChatMessagePacket clientBoundChatMessagePacket) {
        new EventReceiveChatMessage(clientBoundChatMessagePacket).run(getClientConnection());
        GeneralHelper.printChat(clientBoundChatMessagePacket.getMessage());
    }

    public void handleJoinGamePacket(ClientBoundJoinGamePacket clientBoundJoinGamePacket) {
        getClientConnection().getClientPlayer().setEntityId(clientBoundJoinGamePacket.getEntityId());
        getClientConnection().getEventManager().run(clientBoundJoinGamePacket);

        //setup stuff from packet
        ClientPlayer clientPlayer = getClientConnection().getClientPlayer();
        World world = getClientConnection().getWorld();

        world.setDimension(clientBoundJoinGamePacket.getDimension());
        if (clientBoundJoinGamePacket.getDifficulty() != null)
            world.setDifficulty(clientBoundJoinGamePacket.getDifficulty());
        clientPlayer.setGameMode(clientBoundJoinGamePacket.getGameMode());

        //send brand data
        String channel = "minecraft:brand";
        if (ProtocolHandler.getCurrent().getProtocolVer() <= ProtocolHandler.getVersionFromName("1.12.2").getProtocolVer())
            channel = "MC|Brand";
        getClientConnection().sendPacket(new ServerBoundCustomDataPacket(channel, new PacketByteBuf(Unpooled.buffer()).writeString("vanilla")));
    }

    public void handleTabComplete(ClientBoundTabCompletePacket clientBoundTabCompletePacket) {
        getClientConnection().getEventManager().run(clientBoundTabCompletePacket);
    }

    public void handleResourcePackPacket(ClientBoundResourcePackSendPacket clientBoundResourcePackSendPacket) {
        if (clientBoundResourcePackSendPacket.isForced()) {
            //tell the server we have the resource pack if it forces one
            getClientConnection().sendPacket(new ServerBoundResourcePackStatusPacket(ServerBoundResourcePackStatusPacket.ACCEPTED));
            getClientConnection().sendPacket(new ServerBoundResourcePackStatusPacket(ServerBoundResourcePackStatusPacket.SUCCESSFULLY_LOADED));
        }
    }

    public void handlePlayerInfoPacket(ClientBoundPlayerInfoPacket clientBoundPlayerInfoPacket) {
        for (PlayerInfo player : clientBoundPlayerInfoPacket.getPlayers()) {
            switch (clientBoundPlayerInfoPacket.getAction()) {
                case ClientBoundPlayerInfoPacket.ADD_PLAYER -> {
                    if (getClientConnection().getPlayerManager().get(player.getName()) == null) {
                        getClientConnection().getPlayerManager().getPlayers().add(player);
                        new EventAddPlayer(player).run(getClientConnection());
                    }
                }
                case ClientBoundPlayerInfoPacket.REMOVE_PLAYER -> {
                    if (player != null) {
                        getClientConnection().getPlayerManager().getPlayers().remove(player);
                        new EventRemovePlayer(player).run(getClientConnection());
                    }
                }
                case ClientBoundPlayerInfoPacket.UPDATE_GAMEMODE -> {
                    if (player != null) {
                        UUID uuid = player.getUuid();
                        if (GeneralHelper.matchUUIDs(getClientConnection().getSession().getUuid(), uuid.toString())) {
                            getClientConnection().getClientPlayer().setGameMode(player.getGameMode());
                        }
                    }
                }
            }
        }
    }

    public void handleServerDifficultyPacket(ClientBoundServerDifficultyPacket clientBoundServerDifficultyPacket) {
        getClientConnection().getWorld().setDifficulty(clientBoundServerDifficultyPacket.getDifficulty());
    }

    public void handlePingPacket(ClientBoundPingPacket clientBoundPingPacket) {
        getClientConnection().sendPacket(new ServerBoundPongPacket(clientBoundPingPacket.getPacketId()));
    }

    public void handleWorldTimePacket(ClientBoundWorldTimePacket clientBoundWorldTimePacket) {
        getClientConnection().getTpsHelper().worldTime();
    }

    public void handleUpdateHealthPacket(ClientBoundUpdateHealthPacket clientBoundUpdateHealthPacket) {
        if (clientBoundUpdateHealthPacket.getHealth() <= 0) {
            getClientConnection().sendPacket(new ServerBoundClientStatusPacket(ServerBoundClientStatusPacket.RESPAWN));
        }
    }

    public void handleCustomDataPacket(ClientBoundCustomDataPacket clientBoundCustomDataPacket) {
    }

    public void handleRemoveEntitiesPacket(ClientBoundRemoveEntities clientBoundRemoveEntities) {
        for (int entityId : clientBoundRemoveEntities.getEntityIds()) {
            LivingEntity livingEntity = getClientConnection().getWorld().getEntity(entityId);
            if (livingEntity == null)
                return;
            getClientConnection().getWorld().getLivingEntities().remove(livingEntity);
            if (livingEntity instanceof PlayerEntity playerEntity)
                getClientConnection().getWorld().getPlayerEntities().remove(playerEntity);
        }
    }

    public void handleSpawnPlayerPacket(ClientBoundSpawnPlayerPacket clientBoundSpawnPlayerPacket) {
        PlayerInfo playerInfo = getClientConnection().getPlayerManager().get(clientBoundSpawnPlayerPacket.getPlayerUUID());
        if (playerInfo == null)
            return;
        float yaw = (float)(clientBoundSpawnPlayerPacket.getYaw() * 360) / 256.0f;
        float pitch = (float)(clientBoundSpawnPlayerPacket.getPitch() * 360) / 256.0f;
        PlayerEntity player = new PlayerEntity(clientBoundSpawnPlayerPacket.getEntityId(), clientBoundSpawnPlayerPacket.getX(), clientBoundSpawnPlayerPacket.getY(), clientBoundSpawnPlayerPacket.getZ(), yaw, pitch, playerInfo);
        getClientConnection().getWorld().getPlayerEntities().add(player);
        getClientConnection().getWorld().getLivingEntities().add(player);
    }

    public void handleSpawnMobPacket(ClientBoundSpawnMobPacket clientBoundSpawnMobPacket) {
        float yaw = (float)(clientBoundSpawnMobPacket.getYaw() * 360) / 256.0f;
        float pitch = (float)(clientBoundSpawnMobPacket.getPitch() * 360) / 256.0f;
        LivingEntity livingEntity = new LivingEntity(clientBoundSpawnMobPacket.getEntityId(), clientBoundSpawnMobPacket.getX(), clientBoundSpawnMobPacket.getY(), clientBoundSpawnMobPacket.getZ(), yaw, pitch);
        getClientConnection().getWorld().getLivingEntities().add(livingEntity);
    }

    public void handleEntityPositionPacket(ClientBoundEntityPositionPacket clientBoundEntityPositionPacket) {
        int id = clientBoundEntityPositionPacket.getEntityId();
        LivingEntity livingEntity = getClientConnection().getWorld().getEntity(id);
        if (livingEntity != null) {
            double x, y, z;
            if (clientBoundEntityPositionPacket.getOldDeltaX() != -9999) {
                double deltaX = clientBoundEntityPositionPacket.getOldDeltaX();
                double deltaY = clientBoundEntityPositionPacket.getOldDeltaY();
                double deltaZ = clientBoundEntityPositionPacket.getOldDeltaZ();
                x = livingEntity.getX() + deltaX;
                y = livingEntity.getY() + deltaY;
                z = livingEntity.getZ() + deltaZ;
            } else {
                short deltaX = clientBoundEntityPositionPacket.getDeltaX();
                short deltaY = clientBoundEntityPositionPacket.getDeltaY();
                short deltaZ = clientBoundEntityPositionPacket.getDeltaZ();
                x = deltaX == 0 ? livingEntity.getX() : ClientBoundEntityPositionPacket.decodePacketCoordinate(ClientBoundEntityPositionPacket.encodePacketCoordinate(livingEntity.getX()) + deltaX);
                y = deltaY == 0 ? livingEntity.getY() : ClientBoundEntityPositionPacket.decodePacketCoordinate(ClientBoundEntityPositionPacket.encodePacketCoordinate(livingEntity.getY()) + deltaY);
                z = deltaZ == 0 ? livingEntity.getZ() : ClientBoundEntityPositionPacket.decodePacketCoordinate(ClientBoundEntityPositionPacket.encodePacketCoordinate(livingEntity.getZ()) + deltaZ);
            }
            livingEntity.setX(x);
            livingEntity.setY(y);
            livingEntity.setZ(z);
        }
    }

    public void handleEntityPositionAndRotationPacket(ClientBoundEntityPositionAndRotationPacket clientBoundEntityPositionAndRotationPacket) {
        int id = clientBoundEntityPositionAndRotationPacket.getEntityId();
        LivingEntity livingEntity = getClientConnection().getWorld().getEntity(id);
        if (livingEntity != null) {
            double x, y, z;
            if (clientBoundEntityPositionAndRotationPacket.getOldDeltaX() != -9999) {
                double deltaX = clientBoundEntityPositionAndRotationPacket.getOldDeltaX();
                double deltaY = clientBoundEntityPositionAndRotationPacket.getOldDeltaY();
                double deltaZ = clientBoundEntityPositionAndRotationPacket.getOldDeltaZ();
                x = livingEntity.getX() + deltaX;
                y = livingEntity.getY() + deltaY;
                z = livingEntity.getZ() + deltaZ;
            } else {
                short deltaX = clientBoundEntityPositionAndRotationPacket.getDeltaX();
                short deltaY = clientBoundEntityPositionAndRotationPacket.getDeltaY();
                short deltaZ = clientBoundEntityPositionAndRotationPacket.getDeltaZ();
                x = deltaX == 0 ? livingEntity.getX() : ClientBoundEntityPositionPacket.decodePacketCoordinate(ClientBoundEntityPositionPacket.encodePacketCoordinate(livingEntity.getX()) + deltaX);
                y = deltaY == 0 ? livingEntity.getY() : ClientBoundEntityPositionPacket.decodePacketCoordinate(ClientBoundEntityPositionPacket.encodePacketCoordinate(livingEntity.getY()) + deltaY);
                z = deltaZ == 0 ? livingEntity.getZ() : ClientBoundEntityPositionPacket.decodePacketCoordinate(ClientBoundEntityPositionPacket.encodePacketCoordinate(livingEntity.getZ()) + deltaZ);
            }
            livingEntity.setX(x);
            livingEntity.setY(y);
            livingEntity.setZ(z);
        }
    }

    public void handleEntityVelocityPacket(ClientBoundEntityVelocityPacket clientBoundEntityVelocityPacket) {
        int id = clientBoundEntityVelocityPacket.getEntityId();
        LivingEntity livingEntity = getClientConnection().getWorld().getEntity(id);
        if (livingEntity != null) {
            double x = clientBoundEntityVelocityPacket.getVeloX() / 8000.D;
            double y = clientBoundEntityVelocityPacket.getVeloY() / 8000.D;
            double z = clientBoundEntityVelocityPacket.getVeloZ() / 8000.D;
            livingEntity.setX(livingEntity.getX() + x);
            livingEntity.setX(livingEntity.getX() + y);
            livingEntity.setX(livingEntity.getX() + z);
        }
    }

    public void handleEntityTeleportPacket(ClientBoundEntityTeleportPacket clientBoundEntityTeleportPacket) {
        int id = clientBoundEntityTeleportPacket.getEntityId();
        LivingEntity livingEntity = getClientConnection().getWorld().getEntity(id);
        if (livingEntity != null) {
            livingEntity.setX(clientBoundEntityTeleportPacket.getX());
            livingEntity.setY(clientBoundEntityTeleportPacket.getY());
            livingEntity.setZ(clientBoundEntityTeleportPacket.getZ());
            livingEntity.setYaw((clientBoundEntityTeleportPacket.getYaw() * 360) / 256.f);
            livingEntity.setPitch((clientBoundEntityTeleportPacket.getPitch() * 360) / 256.f);
        }
    }

    public void handlePlayerPositionAndLookPacket(ClientBoundPlayerPositionAndLookPacket clientBoundPlayerPositionAndLookPacket) {
        byte flags = clientBoundPlayerPositionAndLookPacket.getFlags();
        boolean xRelative = (flags & 0x01) == 0x01;
        boolean yRelative = (flags & 0x02) == 0x02;
        boolean zRelative = (flags & 0x04) == 0x04;
        boolean yawRelative = (flags & 0x08) == 0x08;
        boolean pitchRelative = (flags & 0x10) == 0x10;

        ClientPlayer clientPlayer = getClientConnection().getClientPlayer();

        if (xRelative)
            clientPlayer.moveX(clientBoundPlayerPositionAndLookPacket.getX());
        else
            clientPlayer.setX(clientBoundPlayerPositionAndLookPacket.getX());

        if (yRelative)
            clientPlayer.moveY(clientBoundPlayerPositionAndLookPacket.getY());
        else
            clientPlayer.setY(clientBoundPlayerPositionAndLookPacket.getY());

        if (zRelative)
            clientPlayer.moveZ(clientBoundPlayerPositionAndLookPacket.getZ());
        else
            clientPlayer.setZ(clientBoundPlayerPositionAndLookPacket.getZ());

        if (yawRelative)
            clientPlayer.moveYaw(clientBoundPlayerPositionAndLookPacket.getYaw());
        else
            clientPlayer.setYaw(clientBoundPlayerPositionAndLookPacket.getYaw());

        if (pitchRelative)
            clientPlayer.movePitch(clientBoundPlayerPositionAndLookPacket.getPitch());
        else
            clientPlayer.setPitch(clientBoundPlayerPositionAndLookPacket.getPitch());

        Packet packet = new ServerBoundConfirmTeleportPacket(clientBoundPlayerPositionAndLookPacket.getTeleportId());
        if (packet.getPacketId() == -1)
            packet = new ServerBoundPlayerPositionAndRotationPacket(clientPlayer.getX(), clientPlayer.getY(), clientPlayer.getZ(), clientPlayer.getYaw(), clientPlayer.getPitch(), true);
        getClientConnection().sendPacket(packet);
    }
}
