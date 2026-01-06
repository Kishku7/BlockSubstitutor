package com.dave.blocksub;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerChunkEvents;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.registry.Registries;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.state.property.Property;

import java.util.Map;

public class ChunkLoadHandler {

    public static void register() {
        ServerChunkEvents.CHUNK_LOAD.register((ServerWorld world, Chunk chunk) -> {
            if (!world.getRegistryKey().equals(World.OVERWORLD)) {
                return;
            }

            int replaced = applySubstitutionsToChunk(world, chunk);
            if (replaced > 0 && ConfigManager.isLogReplacements()) {
                BlockSubMod.LOGGER.info(
                        "[BlockSub] Chunk {} in {}: replaced {} blocks.",
                        chunk.getPos(), world.getRegistryKey().getValue(), replaced
                );
            }
        });
    }

    public static int applySubstitutionsToChunk(ServerWorld world, Chunk chunk) {
        int count = 0;

        for (ChunkSection section : chunk.getSectionArray()) {
            if (section == null || section.isEmpty()) continue;

            for (int y = 0; y < 16; y++) {
                for (int z = 0; z < 16; z++) {
                    for (int x = 0; x < 16; x++) {
                        BlockState oldState = section.getBlockState(x, y, z);
                        Block oldBlock = oldState.getBlock();
                        Identifier oldId = Registries.BLOCK.getId(oldBlock);
                        String replacementIdStr = BlockMapping.getReplacement(oldId.toString());
                        if (replacementIdStr == null) continue;

                        Identifier replId = new Identifier(replacementIdStr);
                        Block replBlock = Registries.BLOCK.get(replId);
                        if (replBlock == null) continue;

                        BlockState newState = copyCompatibleProperties(oldState, replBlock.getDefaultState());
                        section.setBlockState(x, y, z, newState, false);
                        count++;
                    }
                }
            }
        }

        if (count > 0) {
            chunk.setNeedsSaving(true);
        }
        return count;
    }

    private static BlockState copyCompatibleProperties(BlockState oldState, BlockState newState) {
        for (Map.Entry<Property<?>, Comparable<?>> entry : oldState.getEntries().entrySet()) {
            Property<?> property = entry.getKey();
            if (newState.contains(property)) {
                try {
                    newState = withUnchecked(newState, property, entry.getValue());
                } catch (Exception ignored) {}
            }
        }
        return newState;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private static BlockState withUnchecked(BlockState state, Property property, Comparable value) {
        return state.with(property, value);
    }
}
