package me.dustin.chatbot.network.packet.s2c.play;

import io.netty.buffer.Unpooled;
import me.dustin.chatbot.nbt.NbtElement;
import me.dustin.chatbot.network.packet.Packet;
import me.dustin.chatbot.network.packet.handler.ClientBoundPacketHandler;
import me.dustin.chatbot.network.packet.pipeline.PacketByteBuf;
import me.dustin.chatbot.network.world.BlockEntity;
import me.dustin.chatbot.network.world.chunk.Chunk;
import me.dustin.chatbot.network.world.chunk.ChunkSection;
import me.dustin.chatbot.network.world.chunk.Palette;

import java.io.IOException;

public class ClientBoundChunkDataPacket extends Packet.ClientBoundPacket {

    private Chunk chunk;

    public ClientBoundChunkDataPacket(ClientBoundPacketHandler clientBoundPacketHandler) {
        super(clientBoundPacketHandler);
    }

    //packet not finished
    @Override
    public void createPacket(PacketByteBuf packetByteBuf) throws IOException {
        //create a new chunk with the x and z given
        Chunk chunk = new Chunk(packetByteBuf.readInt(), packetByteBuf.readInt());
        //read the heightmap - in NBT, Notch's custom data format that you MUST read properly to get to the next section
        NbtElement heightmap = packetByteBuf.readNbt();
        //size of the amount of bytes for the chunk data
        int sectionsDataSize = packetByteBuf.readVarInt();
        if (sectionsDataSize > 2097152)
            throw new RuntimeException("Chunk data too big!");

        byte[] sectionsData = new byte[sectionsDataSize];
        packetByteBuf.readBytes(sectionsData);

        //find all blocks and add them in the chunk
        PacketByteBuf chunkSectionsByteBuf = new PacketByteBuf(Unpooled.wrappedBuffer(sectionsData));
        for (int i = 0; i < 20; i++) {//i < vertical chunk sections in the world, don't know how to get that yet
            int nonEmptyBlockCount = chunkSectionsByteBuf.readShort();
            if (nonEmptyBlockCount < 0)
                continue;
            byte blockStateContainerBits = chunkSectionsByteBuf.readByte();
            Palette palette = Palette.choosePalette(blockStateContainerBits);
            palette.read(chunkSectionsByteBuf);
            ChunkSection chunkSection = new ChunkSection(nonEmptyBlockCount, palette);
            chunk.addChunkSection(chunkSection);
        }

        //read block entities
        int numBlockEntities = packetByteBuf.readVarInt();
        for (int i = 0; i < numBlockEntities; i++) {
            byte packedCoords = packetByteBuf.readByte();
            int x = (chunk.getChunkX() * 16) + ((packedCoords >> 4) & 15);
            int z = (chunk.getChunkZ() * 16) + (packedCoords & 15);
            int y = packetByteBuf.readShort();
            int type = packetByteBuf.readVarInt();
            NbtElement nbt = packetByteBuf.readNbt();
            BlockEntity blockEntity = new BlockEntity(x, y, z, type, nbt);
            chunk.addBlockEntity(blockEntity);
        }
        getClientConnection().getWorld().addChunk(chunk);
        //then in 1.18 there's light data but who gives a shit
        super.createPacket(packetByteBuf);
    }

    @Override
    public void apply() {
        //((PlayClientBoundPacketHandler)clientBoundPacketHandler).handleChunkDataPacket(this);
    }

    public Chunk getChunk() {
        return chunk;
    }
}
