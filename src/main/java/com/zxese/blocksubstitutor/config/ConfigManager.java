package com.zxese.blocksubstitutor.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;

import java.io.InputStream;
import java.io.InputStreamReader;

import java.nio.file.Files;
import java.nio.file.Path;

/**
 * ConfigManager
 *
 * Handles:
 *   - Loading config from disk
 *   - Loading defaults from resources
 *   - Validating and repairing config
 *   - Saving config back to disk
 *
 * The defaults file (blocksubstitutor-defaults.json) is the authoritative
 * source of truth for schema and default values.
 */
public final class ConfigManager {

    private static final String CONFIG_FILE_NAME = "blocksubstitutor.json";

    private static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .disableHtmlEscaping()
            .create();

    /** The currently loaded config instance. */
    private static BlockSubConfig CURRENT_CONFIG;

    private ConfigManager() {
        // Utility class; no instantiation.
    }

    // ---------------------------------------------------------
    // Public API
    // ---------------------------------------------------------

    /**
     * Loads the config from disk, validates it, repairs it, and writes
     * the repaired version back to disk.
     */
    public static void load(Path configDirectory) {

        Path configPath = configDirectory.resolve(CONFIG_FILE_NAME);

        BlockSubConfig config;

        if (Files.exists(configPath)) {
            config = readConfig(configPath);
        } else {
            config = createDefaultConfig();
        }

        config.validateAndRepair();
        CURRENT_CONFIG = config;

        writeConfig(configPath, config);
    }

    /**
     * Saves the current config to disk without reloading or repairing it.
     * Used by the Cloth Config screen when the user clicks Save.
     */
    public static void save(Path configDirectory) {

        if (CURRENT_CONFIG == null) {
            System.err.println("[BlockSubstitutor] Cannot save config: no config loaded.");
            return;
        }

        Path configPath = configDirectory.resolve(CONFIG_FILE_NAME);
        writeConfig(configPath, CURRENT_CONFIG);
    }

    /**
     * Returns the currently loaded config.
     */
    public static BlockSubConfig getConfig() {
        if (CURRENT_CONFIG == null) {
            CURRENT_CONFIG = createDefaultConfig();
        }
        return CURRENT_CONFIG;
    }

    // ---------------------------------------------------------
    // Reading and Writing
    // ---------------------------------------------------------

    private static BlockSubConfig readConfig(Path path) {

        try (BufferedReader reader = Files.newBufferedReader(path)) {

            BlockSubConfig loaded = GSON.fromJson(reader, BlockSubConfig.class);

            if (loaded == null) {
                System.err.println("[BlockSubstitutor] Config file empty or invalid. Using defaults.");
                return createDefaultConfig();
            }

            return loaded;

        } catch (IOException | JsonSyntaxException e) {
            System.err.println("[BlockSubstitutor] Failed to read config, using defaults: " + e.getMessage());
            return createDefaultConfig();
        }
    }

    private static void writeConfig(Path path, BlockSubConfig config) {

        try {
            Files.createDirectories(path.getParent());

            try (BufferedWriter writer = Files.newBufferedWriter(path)) {
                GSON.toJson(config, writer);
            }

        } catch (IOException e) {
            System.err.println("[BlockSubstitutor] Failed to write config: " + e.getMessage());
        }
    }

    // ---------------------------------------------------------
    // Defaults Loading
    // ---------------------------------------------------------

    /**
     * Loads the default config from the mod resources.
     */
    public static BlockSubConfig loadDefaultsFromResource() {

        try (InputStream in = ConfigManager.class.getClassLoader()
                .getResourceAsStream("blocksubstitutor-defaults.json")) {

            if (in == null) {
                System.err.println("[BlockSubstitutor] ERROR: Default config resource missing.");
                return new BlockSubConfig(
                        1,
                        BlockSubConfig.getFallbackDefaults(),
                        true
                );
            }

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {

                BlockSubConfig defaults = GSON.fromJson(reader, BlockSubConfig.class);

                if (defaults == null) {
                    System.err.println("[BlockSubstitutor] ERROR: Default config JSON invalid.");
                    return new BlockSubConfig(
                            1,
                            BlockSubConfig.getFallbackDefaults(),
                            true
                    );
                }

                return defaults;
            }

        } catch (Exception e) {
            System.err.println("[BlockSubstitutor] Failed to load defaults: " + e.getMessage());
            return new BlockSubConfig(
                    1,
                    BlockSubConfig.getFallbackDefaults(),
                    true
            );
        }
    }

    /**
     * Creates a new config instance using the defaults file.
     */
    private static BlockSubConfig createDefaultConfig() {
        BlockSubConfig defaults = loadDefaultsFromResource();
        defaults.validateAndRepair();
        return defaults;
    }
}