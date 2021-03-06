package me.dustin.chatbot.network.packet.impl.play.s2c;

import io.netty.buffer.Unpooled;
import me.dustin.chatbot.nbt.NbtElement;
import me.dustin.chatbot.network.packet.Packet;
import me.dustin.chatbot.network.packet.handler.ClientBoundPacketHandler;
import me.dustin.chatbot.network.packet.handler.PlayClientBoundPacketHandler;
import me.dustin.chatbot.network.packet.pipeline.PacketByteBuf;
import me.dustin.chatbot.block.BlockEntity;
import me.dustin.chatbot.world.chunk.Chunk;
import me.dustin.chatbot.world.chunk.ChunkSection;
import me.dustin.chatbot.world.chunk.Palette;

public class ClientBoundChunkDataPacket extends Packet.ClientBoundPacket {
    private final Chunk chunk;

    //packet not finished
    public ClientBoundChunkDataPacket(PacketByteBuf packetByteBuf) {
        super(packetByteBuf);//create a new chunk with the x and z given
        Chunk chunk = new Chunk(packetByteBuf.readInt(), packetByteBuf.readInt());
        //read the heightmap - in NBT, Notch's custom data format that you MUST read properly to get to the next section
        NbtElement heightmap = packetByteBuf.readNbt();
        //size of the amount of bytes for the chunk data
        int sectionsDataSize = packetByteBuf.readVarInt();
        if (sectionsDataSize > 2097152)
            throw new RuntimeException("Chunk data too big!");

        byte[] sectionsData = new byte[sectionsDataSize];
        packetByteBuf.readBytes(sectionsData);
        PacketByteBuf chunkSectionsByteBuf = new PacketByteBuf(Unpooled.wrappedBuffer(sectionsData));

        //find all blocks and add them in the chunk
        int vSections = getClientConnection().getWorld().countVerticalSections();
        for (int i = 0; i < vSections; i++) {
            int nonEmptyBlockCount = chunkSectionsByteBuf.readShort();
            if (nonEmptyBlockCount < 0)
                continue;
            byte blockStateContainerBits = chunkSectionsByteBuf.readByte();
            Palette palette = Palette.choosePalette(blockStateContainerBits);
            palette.fromPacket(chunkSectionsByteBuf);
            long[] data;
            if (blockStateContainerBits > 0) {
                int elementsPerLong = (char)(64 / blockStateContainerBits);
                int containerSize = 1 << 4 * 3;
                int j = (containerSize + elementsPerLong - 1) / elementsPerLong;
                data = new long[j];
            } else {
                data = new long[0];
            }
            chunkSectionsByteBuf.readLongArray(data);
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
        //then in 1.18 there's light data but who gives a shit
        this.chunk = chunk;
    }

    @Override
    public void handlePacket(ClientBoundPacketHandler clientBoundPacketHandler) {
        ((PlayClientBoundPacketHandler)clientBoundPacketHandler).handleChunkDataPacket(this);
    }

    public Chunk getChunk() {
        return chunk;
    }
}
