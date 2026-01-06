package com.dave.blocksub;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerChunkManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;

import java.util.HashSet;
import java.util.Set;

public class StartupWorldScanner {

    public static void register() {
        ServerLifecycleEvents.SERVER_STARTED.register(StartupWorldScanner::onServerStarted);
    }

    private static void onServerStarted(MinecraftServer server) {
        if (!ConfigManager.isScanOverworldOnStartup()) {
            BlockSubMod.LOGGER.info("[BlockSub] Startup overworld scan disabled in config.");
            return;
        }

        ServerWorld overworld = server.getWorld(World.OVERWORLD);
        if (overworld == null) {
            BlockSubMod.LOGGER.warn("[BlockSub] Overworld not found; skipping startup scan.");
            return;
        }

        BlockSubMod.LOGGER.info("[BlockSub] Starting initial overworld scan for block substitutions...");

        ServerChunkManager chunkManager = overworld.getChunkManager();
        Set<ChunkPos> loaded = new HashSet<>(chunkManager.getLoadedChunks());

        int totalReplaced = 0;
        for (ChunkPos pos : loaded) {
            totalReplaced += ChunkLoadHandler.applySubstitutionsToChunk(
                    overworld, overworld.getChunk(pos.x, pos.z)
            );
        }

        if (totalReplaced > 0) {
            BlockSubMod.LOGGER.info("[BlockSub] Startup scan replaced {} blocks in overworld.", totalReplaced);
        } else {
            BlockSubMod.LOGGER.info("[BlockSub] Startup scan found no blocks to replace.");
        }
    }
}
