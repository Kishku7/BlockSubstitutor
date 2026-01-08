package com.zxese.blocksubstitutor.config;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * BlockSubConfig
 *
 * Holds all configuration values for BlockSubstitutor.
 * This includes:
 *   - Block downgrade mappings
 *   - Replacement logging toggle
 *
 * The defaults are loaded from blocksubstitutor-defaults.json.
 */
public final class BlockSubConfig {

    private int configVersion;

    /** Mapping: source block ID -> replacement block ID */
    private Map<String, String> blockMappings;

    /** Whether to log replacement summaries */
    private boolean logReplacements;

    /**
     * Default constructor for Gson.
     */
    public BlockSubConfig() {
        this.configVersion = 1;
        this.blockMappings = new HashMap<>();
        this.logReplacements = true;
    }

    /**
     * Primary constructor.
     */
    public BlockSubConfig(
            int configVersion,
            Map<String, String> blockMappings,
            boolean logReplacements
    ) {
        this.configVersion = configVersion;
        this.blockMappings = new HashMap<>(blockMappings);
        this.logReplacements = logReplacements;
    }

    // ---------------------------------------------------------
    // Getters
    // ---------------------------------------------------------

    public int getConfigVersion() {
        return configVersion;
    }

    public Map<String, String> getBlockMappings() {
        return Collections.unmodifiableMap(blockMappings);
    }

    public boolean isLogReplacements() {
        return logReplacements;
    }

    // ---------------------------------------------------------
    // Setters (used by Gson and config screen)
    // ---------------------------------------------------------

    public void setConfigVersion(int configVersion) {
        this.configVersion = configVersion;
    }

    public void setBlockMappings(Map<String, String> blockMappings) {
        this.blockMappings = new HashMap<>(blockMappings);
    }

    public void setLogReplacements(boolean value) {
        this.logReplacements = value;
    }

    // ---------------------------------------------------------
    // Validation and Repair
    // ---------------------------------------------------------

    /**
     * Validates and repairs this config using the defaults file as schema.
     */
    public void validateAndRepair() {

        if (blockMappings == null) {
            blockMappings = new HashMap<>();
        }

        BlockSubConfig defaults = ConfigManager.loadDefaultsFromResource();
        Map<String, String> defaultMappings = defaults.getBlockMappings();

        // Remove invalid entries
        blockMappings.entrySet().removeIf(entry ->
                entry.getKey() == null ||
                entry.getKey().trim().isEmpty() ||
                entry.getValue() == null ||
                entry.getValue().trim().isEmpty()
        );

        // Add missing default mappings
        for (Map.Entry<String, String> entry : defaultMappings.entrySet()) {
            blockMappings.putIfAbsent(entry.getKey(), entry.getValue());
        }

        // Ensure config version is current
        this.configVersion = 1;
    }

    // ---------------------------------------------------------
    // Lookup Helper
    // ---------------------------------------------------------

    public String getReplacement(String sourceBlockId) {
        if (sourceBlockId == null) {
            return null;
        }
        return blockMappings.get(sourceBlockId);
    }

    // ---------------------------------------------------------
    // Fallback Defaults
    // ---------------------------------------------------------

    public static Map<String, String> getFallbackDefaults() {
        Map<String, String> map = new HashMap<>();

        map.put("minecraft:crafter", "minecraft:crafting_table");
        map.put("minecraft:copper_door", "minecraft:iron_door");
        map.put("minecraft:copper_bulb", "minecraft:ochre_froglight");
        map.put("minecraft:resin_clump", "minecraft:nether_brick");

        return Collections.unmodifiableMap(map);
    }

    // ---------------------------------------------------------
    // Utility Overrides
    // ---------------------------------------------------------

    @Override
    public String toString() {
        return "BlockSubConfig{" +
                "configVersion=" + configVersion +
                ", blockMappings=" + blockMappings.size() +
                ", logReplacements=" + logReplacements +
                "}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BlockSubConfig)) return false;
        BlockSubConfig that = (BlockSubConfig) o;
        return configVersion == that.configVersion &&
                logReplacements == that.logReplacements &&
                Objects.equals(blockMappings, that.blockMappings);
    }

    @Override
    public int hashCode() {
        return Objects.hash(configVersion, blockMappings, logReplacements);
    }
}