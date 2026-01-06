package com.dave.blocksub;

import com.google.gson.*;
import net.fabricmc.loader.api.FabricLoader;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ConfigManager {

    private static final String CONFIG_FILE_NAME = "block_substitutor.json";
    private static final String DEFAULT_CONFIG_PATH = "/assets/block_substitutor/default_config.json";

    private static Map<String, String> mappings = Collections.emptyMap();
    private static boolean logReplacements = true;
    private static boolean scanOverworldOnStartup = true;

    public static void load() {
        try {
            Path configDir = FabricLoader.getInstance().getConfigDir();
            Path configPath = configDir.resolve(CONFIG_FILE_NAME);

            if (Files.notExists(configPath)) {
                BlockSubMod.LOGGER.info("[BlockSub] Config file not found, writing default to {}", configPath);
                writeDefaultConfig(configPath);
            }

            try (Reader reader = Files.newBufferedReader(configPath, StandardCharsets.UTF_8)) {
                JsonObject root = JsonParser.parseReader(reader).getAsJsonObject();

                JsonObject mappingsObj = root.getAsJsonObject("mappings");
                Map<String, String> map = new HashMap<>();
                if (mappingsObj != null) {
                    for (Map.Entry<String, JsonElement> e : mappingsObj.entrySet()) {
                        if (e.getValue().isJsonPrimitive()) {
                            map.put(e.getKey(), e.getValue().getAsString());
                        }
                    }
                }
                mappings = Collections.unmodifiableMap(map);

                JsonObject optionsObj = root.getAsJsonObject("options");
                if (optionsObj != null) {
                    if (optionsObj.has("log_replacements")) {
                        logReplacements = optionsObj.get("log_replacements").getAsBoolean();
                    }
                    if (optionsObj.has("scan_overworld_on_startup")) {
                        scanOverworldOnStartup = optionsObj.get("scan_overworld_on_startup").getAsBoolean();
                    }
                }

                BlockSubMod.LOGGER.info(
                        "[BlockSub] Loaded config: {} mappings, logReplacements={}, scanOnStartup={}",
                        mappings.size(), logReplacements, scanOverworldOnStartup
                );
            }
        } catch (Exception e) {
            BlockSubMod.LOGGER.error("[BlockSub] Failed to load config, using defaults", e);
            mappings = Collections.emptyMap();
            logReplacements = true;
            scanOverworldOnStartup = true;
        }
    }

    public static void save() {
        try {
            Path configDir = FabricLoader.getInstance().getConfigDir();
            Path configPath = configDir.resolve(CONFIG_FILE_NAME);

            JsonObject root = new JsonObject();
            root.addProperty("__comment", "BlockSubstitutor configuration file. Edit mappings to customize block replacements.");

            JsonObject mappingsObj = new JsonObject();
            for (Map.Entry<String, String> e : mappings.entrySet()) {
                mappingsObj.addProperty(e.getKey(), e.getValue());
            }
            root.add("mappings", mappingsObj);

            JsonObject optionsObj = new JsonObject();
            optionsObj.addProperty("log_replacements", logReplacements);
            optionsObj.addProperty("scan_overworld_on_startup", scanOverworldOnStartup);
            root.add("options", optionsObj);

            Files.createDirectories(configPath.getParent());
            try (Writer writer = Files.newBufferedWriter(configPath, StandardCharsets.UTF_8)) {
                new GsonBuilder().setPrettyPrinting().create().toJson(root, writer);
            }

            BlockSubMod.LOGGER.info("[BlockSub] Saved config to {}", configPath);
        } catch (Exception e) {
            BlockSubMod.LOGGER.error("[BlockSub] Failed to save config", e);
        }
    }

    private static void writeDefaultConfig(Path configPath) {
        try (InputStream in = ConfigManager.class.getResourceAsStream(DEFAULT_CONFIG_PATH)) {
            if (in == null) {
                BlockSubMod.LOGGER.error("[BlockSub] Default config resource not found at {}", DEFAULT_CONFIG_PATH);
                return;
            }
            Files.createDirectories(configPath.getParent());
            Files.copy(in, configPath);
        } catch (IOException e) {
            BlockSubMod.LOGGER.error("[BlockSub] Failed to write default config", e);
        }
    }

    public static Map<String, String> getMappings() {
        return mappings;
    }

    public static void setMappings(Map<String, String> newMappings) {
        mappings = Collections.unmodifiableMap(new HashMap<>(newMappings));
    }

    public static boolean isLogReplacements() {
        return logReplacements;
    }

    public static void setLogReplacements(boolean value) {
        logReplacements = value;
    }

    public static boolean isScanOverworldOnStartup() {
        return scanOverworldOnStartup;
    }

    public static void setScanOverworldOnStartup(boolean value) {
        scanOverworldOnStartup = value;
    }
}
