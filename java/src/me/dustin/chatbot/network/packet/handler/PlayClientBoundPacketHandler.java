package me.dustin.chatbot.network.packet.handler;

import io.netty.buffer.Unpooled;
import me.dustin.chatbot.ChatBot;
import me.dustin.chatbot.chat.ChatMessage;
import me.dustin.chatbot.entity.Entity;
import me.dustin.chatbot.entity.LivingEntity;
import me.dustin.chatbot.entity.player.PlayerEntity;
import me.dustin.chatbot.event.EventAddPlayer;
import me.dustin.chatbot.event.EventReceiveChatMessage;
import me.dustin.chatbot.event.EventRemovePlayer;
import me.dustin.chatbot.helper.GeneralHelper;
import me.dustin.chatbot.nbt.NbtCompound;
import me.dustin.chatbot.nbt.NbtElement;
import me.dustin.chatbot.nbt.NbtList;
import me.dustin.chatbot.network.ProtocolHandler;
import me.dustin.chatbot.network.packet.impl.play.c2s.*;
import me.dustin.chatbot.network.packet.impl.play.s2c.*;
import me.dustin.chatbot.network.packet.pipeline.PacketByteBuf;
import me.dustin.chatbot.entity.player.ClientPlayer;
import me.dustin.chatbot.entity.player.PlayerInfo;
import me.dustin.chatbot.world.World;

import java.time.Instant;
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

    public void handleGameMessagePacket(ClientBoundGameMessagePacket clientBoundGameMessagePacket) {
        GeneralHelper.printChat(clientBoundGameMessagePacket.getMessage());
    }

    public void handleJoinGamePacket(ClientBoundJoinGamePacket clientBoundJoinGamePacket) {
        //setup stuff from packet
        ClientPlayer clientPlayer = getClientConnection().getClientPlayer();
        clientPlayer.setEntityId(clientBoundJoinGamePacket.getEntityId());
        clientPlayer.setGameMode(clientBoundJoinGamePacket.getGameMode());
        //world
        World world = getClientConnection().getWorld();
        world.setDimension(clientBoundJoinGamePacket.getDimension());
        NbtCompound dimCompound = clientBoundJoinGamePacket.getDimensionNbt();
        if (dimCompound != null) {
            if (dimCompound.has("min_y"))
                world.setMinY(clientBoundJoinGamePacket.getDimension(), (int) dimCompound.get("min_y").getValue());
            if (dimCompound.has("height"))
                world.setWorldHeight(clientBoundJoinGamePacket.getDimension(), (int) dimCompound.get("height").getValue());
        } else {
            dimCompound = (NbtCompound)clientBoundJoinGamePacket.getDimensionCodec().get("minecraft:dimension_type");
            NbtList list = (NbtList) dimCompound.get("value");
            for (NbtElement element : list.getValue()) {
                NbtCompound compound = (NbtCompound)element;
                String name = (String)compound.get("name").getValue();
                World.Dimension dim = World.Dimension.get(name);
                NbtCompound data = (NbtCompound)compound.get("element");
                if (data.has("min_y"))
                    world.setMinY(dim, (int) data.get("min_y").getValue());
                if (data.has("height"))
                    world.setWorldHeight(dim, (int) data.get("height").getValue());
            }
        }
        if (clientBoundJoinGamePacket.getDifficulty() != null)
            world.setDifficulty(clientBoundJoinGamePacket.getDifficulty());
        //send settings
        getClientConnection().sendPacket(new ServerBoundClientSettingsPacket(ChatBot.getConfig().getLocale(), ChatBot.getConfig().isAllowServerListing(), ServerBoundClientSettingsPacket.SkinPart.all()));
        //send brand data
        String channel = "minecraft:brand";
        if (ProtocolHandler.getCurrent().getProtocolVer() <= ProtocolHandler.getVersionFromName("1.12.2").getProtocolVer())
            channel = "MC|Brand";
        getClientConnection().sendPacket(new ServerBoundCustomDataPacket(channel, new PacketByteBuf(Unpooled.buffer()).writeString("vanilla")));

        if (!getClientConnection().isInGame()) {//fix for servers that pass you through proxy servers spamming the console
            GeneralHelper.print("Received Join Game. Loading processes.", ChatMessage.TextColor.GOLD);
        }
        getClientConnection().getEventManager().run(clientBoundJoinGamePacket);
    }

    public void handleTabComplete(ClientBoundTabCompletePacket clientBoundTabCompletePacket) {
        getClientConnection().getEventManager().run(clientBoundTabCompletePacket);
    }

    public void handleResourcePackPacket(ClientBoundResourcePackSendPacket clientBoundResourcePackSendPacket) {
        if (clientBoundResourcePackSendPacket.isForced()) {
            GeneralHelper.print("Server is forcing resource pack, telling server we have it...", ChatMessage.TextColor.GREEN);
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

    public void handleSetHotbarSlotPacket(ClientBoundSetHotbarSlotPacket clientBoundSetHotbarSlotPacket) {
        getClientConnection().sendPacket(new ServerBoundSetHotbarSlotPacket(clientBoundSetHotbarSlotPacket.getSlot()));
    }

    public void handleUpdateHealthPacket(ClientBoundUpdateHealthPacket clientBoundUpdateHealthPacket) {
        if (clientBoundUpdateHealthPacket.getHealth() <= 0) {
            getClientConnection().sendPacket(new ServerBoundClientStatusPacket(ServerBoundClientStatusPacket.RESPAWN));
        }
    }

    public void handleCustomDataPacket(ClientBoundCustomDataPacket clientBoundCustomDataPacket) {}

    public void handleRemoveEntitiesPacket(ClientBoundRemoveEntities clientBoundRemoveEntities) {
        for (int entityId : clientBoundRemoveEntities.getEntityIds()) {
            Entity entity = getClientConnection().getWorld().getEntity(entityId);
            if (entity == null)
                return;
            getClientConnection().getWorld().getEntities().remove(entity);
            if (entity instanceof PlayerEntity playerEntity)
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
        getClientConnection().getWorld().getEntities().add(player);
    }

    public void handleSpawnEntityPacket(ClientBoundSpawnEntityPacket clientBoundSpawnEntityPacket) {
        float yaw = (float)(clientBoundSpawnEntityPacket.getYaw() * 360) / 256.0f;
        float pitch = (float)(clientBoundSpawnEntityPacket.getPitch() * 360) / 256.0f;
        String typeName = Entity.getTypeName(clientBoundSpawnEntityPacket.getType());
        Entity entity;
        //check if living entity because since (22w14a?) ClientBoundSpawnMobPacket got merged into this
        if (Entity.isLiving(typeName)) {
            entity = new LivingEntity(clientBoundSpawnEntityPacket.getEntityId(), typeName, clientBoundSpawnEntityPacket.getX(), clientBoundSpawnEntityPacket.getY(), clientBoundSpawnEntityPacket.getZ(), yaw, pitch);
        } else {
            entity = new Entity(clientBoundSpawnEntityPacket.getEntityId(), typeName, clientBoundSpawnEntityPacket.getX(), clientBoundSpawnEntityPacket.getY(), clientBoundSpawnEntityPacket.getZ(), yaw, pitch);
        }
        getClientConnection().getWorld().getEntities().add(entity);
    }

    //no longer exists in 1.19, merged into handleSpawnEntityPacket instead
    public void handleSpawnMobPacket(ClientBoundSpawnMobPacket clientBoundSpawnMobPacket) {
        float yaw = (float)(clientBoundSpawnMobPacket.getYaw() * 360) / 256.0f;
        float pitch = (float)(clientBoundSpawnMobPacket.getPitch() * 360) / 256.0f;
        String typeName = Entity.getTypeName(clientBoundSpawnMobPacket.getType());
        LivingEntity livingEntity = new LivingEntity(clientBoundSpawnMobPacket.getEntityId(), typeName, clientBoundSpawnMobPacket.getX(), clientBoundSpawnMobPacket.getY(), clientBoundSpawnMobPacket.getZ(), yaw, pitch);
        getClientConnection().getWorld().getEntities().add(livingEntity);
    }

    public void handleEntityPositionPacket(ClientBoundEntityPositionPacket clientBoundEntityPositionPacket) {
        int id = clientBoundEntityPositionPacket.getEntityId();
        Entity entity = getClientConnection().getWorld().getEntity(id);
        if (entity != null) {
            double x, y, z;
            if (clientBoundEntityPositionPacket.getOldDeltaX() != -9999) {
                double deltaX = clientBoundEntityPositionPacket.getOldDeltaX();
                double deltaY = clientBoundEntityPositionPacket.getOldDeltaY();
                double deltaZ = clientBoundEntityPositionPacket.getOldDeltaZ();
                x = entity.getX() + deltaX;
                y = entity.getY() + deltaY;
                z = entity.getZ() + deltaZ;
            } else {
                short deltaX = clientBoundEntityPositionPacket.getDeltaX();
                short deltaY = clientBoundEntityPositionPacket.getDeltaY();
                short deltaZ = clientBoundEntityPositionPacket.getDeltaZ();
                x = deltaX == 0 ? entity.getX() : ClientBoundEntityPositionPacket.decodePacketCoordinate(ClientBoundEntityPositionPacket.encodePacketCoordinate(entity.getX()) + deltaX);
                y = deltaY == 0 ? entity.getY() : ClientBoundEntityPositionPacket.decodePacketCoordinate(ClientBoundEntityPositionPacket.encodePacketCoordinate(entity.getY()) + deltaY);
                z = deltaZ == 0 ? entity.getZ() : ClientBoundEntityPositionPacket.decodePacketCoordinate(ClientBoundEntityPositionPacket.encodePacketCoordinate(entity.getZ()) + deltaZ);
            }
            entity.setX(x);
            entity.setY(y);
            entity.setZ(z);
        }
    }

    public void handleEntityPositionAndRotationPacket(ClientBoundEntityPositionAndRotationPacket clientBoundEntityPositionAndRotationPacket) {
        int id = clientBoundEntityPositionAndRotationPacket.getEntityId();
        Entity entity = getClientConnection().getWorld().getEntity(id);
        if (entity != null) {
            double x, y, z;
            if (clientBoundEntityPositionAndRotationPacket.getOldDeltaX() != -9999) {
                double deltaX = clientBoundEntityPositionAndRotationPacket.getOldDeltaX();
                double deltaY = clientBoundEntityPositionAndRotationPacket.getOldDeltaY();
                double deltaZ = clientBoundEntityPositionAndRotationPacket.getOldDeltaZ();
                x = entity.getX() + deltaX;
                y = entity.getY() + deltaY;
                z = entity.getZ() + deltaZ;
            } else {
                short deltaX = clientBoundEntityPositionAndRotationPacket.getDeltaX();
                short deltaY = clientBoundEntityPositionAndRotationPacket.getDeltaY();
                short deltaZ = clientBoundEntityPositionAndRotationPacket.getDeltaZ();
                x = deltaX == 0 ? entity.getX() : ClientBoundEntityPositionPacket.decodePacketCoordinate(ClientBoundEntityPositionPacket.encodePacketCoordinate(entity.getX()) + deltaX);
                y = deltaY == 0 ? entity.getY() : ClientBoundEntityPositionPacket.decodePacketCoordinate(ClientBoundEntityPositionPacket.encodePacketCoordinate(entity.getY()) + deltaY);
                z = deltaZ == 0 ? entity.getZ() : ClientBoundEntityPositionPacket.decodePacketCoordinate(ClientBoundEntityPositionPacket.encodePacketCoordinate(entity.getZ()) + deltaZ);
            }
            entity.setX(x);
            entity.setY(y);
            entity.setZ(z);
        }
    }

    public void handleEntityVelocityPacket(ClientBoundEntityVelocityPacket clientBoundEntityVelocityPacket) {
        int id = clientBoundEntityVelocityPacket.getEntityId();
        Entity entity = getClientConnection().getWorld().getEntity(id);
        if (entity != null) {
            double x = clientBoundEntityVelocityPacket.getVeloX() / 8000.D;
            double y = clientBoundEntityVelocityPacket.getVeloY() / 8000.D;
            double z = clientBoundEntityVelocityPacket.getVeloZ() / 8000.D;
            entity.setX(entity.getX() + x);
            entity.setX(entity.getX() + y);
            entity.setX(entity.getX() + z);
        }
    }

    public void handleEntityTeleportPacket(ClientBoundEntityTeleportPacket clientBoundEntityTeleportPacket) {
        int id = clientBoundEntityTeleportPacket.getEntityId();
        Entity entity = getClientConnection().getWorld().getEntity(id);
        if (entity != null) {
            entity.setX(clientBoundEntityTeleportPacket.getX());
            entity.setY(clientBoundEntityTeleportPacket.getY());
            entity.setZ(clientBoundEntityTeleportPacket.getZ());
            entity.setYaw((clientBoundEntityTeleportPacket.getYaw() * 360) / 256.f);
            entity.setPitch((clientBoundEntityTeleportPacket.getPitch() * 360) / 256.f);
        }
    }

    public void handleChunkDataPacket(ClientBoundChunkDataPacket clientBoundChunkDataPacket) {
        System.out.println("chunk: " + clientBoundChunkDataPacket.getChunk().getChunkX() + " " + clientBoundChunkDataPacket.getChunk().getChunkZ());
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
        getClientConnection().sendPacket(new ServerBoundConfirmTeleportPacket(clientBoundPlayerPositionAndLookPacket.getTeleportId()));
        getClientConnection().sendPacket(new ServerBoundPlayerPositionAndRotationPacket(clientPlayer.getX(), clientPlayer.getY(), clientPlayer.getZ(), clientPlayer.getYaw(), clientPlayer.getPitch(), true));
        //supposedly the vanilla client sends a RESPAWN as it loads the world up
        if (!clientPlayer.hasSetPos()) {
            getClientConnection().sendPacket(new ServerBoundClientStatusPacket(ServerBoundClientStatusPacket.RESPAWN));
        }
        clientPlayer.setHasSetPos(true);
    }
}
