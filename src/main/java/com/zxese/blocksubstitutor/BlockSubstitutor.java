package com.zxese.blocksubstitutor;

import com.zxese.blocksubstitutor.config.BlockSubConfig;
import com.zxese.blocksubstitutor.config.ConfigManager;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerChunkEvents;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;

import net.minecraft.nbt.NbtCompound;

import net.minecraft.registry.Registries;

import net.minecraft.server.world.ServerWorld;

import net.minecraft.state.property.Property;

import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

import net.minecraft.world.chunk.WorldChunk;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * BlockSubstitutor
 *
 * This class implements the downgrade engine for all dimensions.
 * It performs block replacement on chunk load.
 *
 * Features:
 *   - Full property preservation
 *   - Block entity NBT preservation
 *   - Missing property logging (once per block pair)
 *   - Summary-style replacement logging
 *   - All-dimension support
 */
public class BlockSubstitutor implements ModInitializer {

    /**
     * Tracks which missing properties have already been logged for each
     * original -> replacement pair. Prevents log spam.
     *
     * Key format: "minecraft:oldblock -> minecraft:newblock"
     * Value: Set of property names already logged.
     */
    private final Map<String, Set<String>> missingPropertyLog = new HashMap<>();

    @Override
    public void onInitialize() {

        // ---------------------------------------------------------
        // 1. Load config at startup
        // ---------------------------------------------------------
        ConfigManager.load(
                net.fabricmc.loader.api.FabricLoader.getInstance().getConfigDir()
        );

        // ---------------------------------------------------------
        // 2. Register chunk-load scanning (core behavior)
        // ---------------------------------------------------------
        ServerChunkEvents.CHUNK_LOAD.register(this::replaceBlocksInChunk);

        System.out.println("[BlockSubstitutor] Initialized (chunk-load scanning active).");
    }

    // =========================================================
    // Chunk Scan
    // =========================================================

    /**
     * Scans a single chunk and replaces blocks according to the config mapping.
     * This is called on each chunk load.
     */
    private void replaceBlocksInChunk(ServerWorld world, WorldChunk chunk) {

        BlockSubConfig config = ConfigManager.getConfig();
        Map<String, String> mappings = config.getBlockMappings();

        if (mappings.isEmpty()) {
            return;
        }

        boolean logReplacements = config.isLogReplacements();

        // Summary counter: "old -> new" -> count
        Map<String, Integer> replacementCounts = new HashMap<>();

        int startX = chunk.getPos().getStartX();
        int startZ = chunk.getPos().getStartZ();

        for (int dx = 0; dx < 16; dx++) {
            for (int dz = 0; dz < 16; dz++) {
                for (int y = world.getBottomY(); y < world.getTopY(); y++) {

                    BlockPos pos = new BlockPos(startX + dx, y, startZ + dz);
                    BlockState originalState = world.getBlockState(pos);

                    Identifier originalId = Registries.BLOCK.getId(originalState.getBlock());
                    if (originalId == null) {
                        continue;
                    }

                    String originalIdStr = originalId.toString();
                    if (!mappings.containsKey(originalIdStr)) {
                        continue;
                    }

                    String replacementIdStr = mappings.get(originalIdStr);
                    Block replacementBlock = Registries.BLOCK.get(new Identifier(replacementIdStr));
                    if (replacementBlock == null) {
                        continue;
                    }

                    replaceSingleBlock(
                            world,
                            pos,
                            originalState,
                            originalIdStr,
                            replacementBlock,
                            replacementIdStr
                    );

                    // Count replacement for summary
                    String key = originalIdStr + " -> " + replacementIdStr;
                    replacementCounts.put(key, replacementCounts.getOrDefault(key, 0) + 1);
                }
            }
        }

        // Print summary if enabled
        if (logReplacements && !replacementCounts.isEmpty()) {
            System.out.println(
                    "[BlockSubstitutor] Replacement summary for chunk "
                            + chunk.getPos().x + ", " + chunk.getPos().z
                            + " in dimension " + world.getRegistryKey().getValue() + ":"
            );

            for (Map.Entry<String, Integer> entry : replacementCounts.entrySet()) {
                System.out.println("  " + entry.getKey() + " : " + entry.getValue());
            }
        }
    }

    // =========================================================
    // Block Replacement Logic
    // =========================================================

    /**
     * Replaces a single block at a position with full property and block entity preservation.
     */
    private void replaceSingleBlock(
            ServerWorld world,
            BlockPos pos,
            BlockState originalState,
            String originalId,
            Block replacementBlock,
            String replacementId
    ) {
        BlockState replacementBaseState = replacementBlock.getDefaultState();

        BlockState finalState = copySharedProperties(
                originalId,
                replacementId,
                originalState,
                replacementBaseState
        );

        BlockEntity originalBe = world.getBlockEntity(pos);
        NbtCompound beNbt = null;

        if (originalBe != null) {
            beNbt = originalBe.createNbtWithId();
        }

        world.setBlockState(pos, finalState, Block.NOTIFY_ALL);

        if (beNbt != null) {
            BlockEntity newBe = world.getBlockEntity(pos);
            if (newBe != null) {
                newBe.readNbt(beNbt);
                newBe.markDirty();
            }
        }
    }

    /**
     * Copies all shared properties from the original blockstate to the replacement blockstate.
     */
    private BlockState copySharedProperties(
            String originalId,
            String replacementId,
            BlockState from,
            BlockState to
    ) {
        for (Property<?> property : from.getProperties()) {

            if (to.contains(property)) {
                to = safelyApplyProperty(from, to, property);
            } else {
                logMissingPropertyOnce(originalId, replacementId, property.getName());
            }
        }

        return to;
    }

    /**
     * Safely applies a property value from one blockstate to another.
     */
    private <T extends Comparable<T>> BlockState safelyApplyProperty(
            BlockState from,
            BlockState to,
            Property<T> property
    ) {
        T value = from.get(property);

        try {
            return to.with(property, value);
        } catch (Exception e) {
            return to;
        }
    }

    /**
     * Logs a missing property once per original -> replacement pair.
     */
    private void logMissingPropertyOnce(
            String originalId,
            String replacementId,
            String propertyName
    ) {
        String key = originalId + " -> " + replacementId;

        Set<String> alreadyLogged =
                missingPropertyLog.computeIfAbsent(key, k -> new HashSet<>());

        if (alreadyLogged.add(propertyName)) {
            System.out.println(
                    "[BlockSubstitutor] Property '"
                    + propertyName
                    + "' from "
                    + originalId
                    + " is not supported by "
                    + replacementId
                    + " (future messages suppressed for this pair)"
            );
        }
    }
}