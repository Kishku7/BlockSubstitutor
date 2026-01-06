package com.dave.blocksub.client;

import com.dave.blocksub.ConfigManager;
import com.dave.blocksub.BlockSubMod;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class BlockSubConfigScreen {

    private static class MappingEntry {
        String key;
        String value;

        MappingEntry(String key, String value) {
            this.key = key;
            this.value = value;
        }
    }

    public static Screen build(Screen parent) {
        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(Text.literal("Block Substitutor Config"));

        ConfigEntryBuilder entryBuilder = builder.entryBuilder();

        // Copy current config into mutable structures
        Map<String, String> originalMappings = new LinkedHashMap<>(ConfigManager.getMappings());
        List<MappingEntry> mappingEntries = new ArrayList<>();
        for (Map.Entry<String, String> e : originalMappings.entrySet()) {
            mappingEntries.add(new MappingEntry(e.getKey(), e.getValue()));
        }

        // OPTIONS CATEGORY
        ConfigCategory optionsCat = builder.getOrCreateCategory(Text.literal("Options"));

        optionsCat.addEntry(
                entryBuilder
                        .startBooleanToggle(Text.literal("Log replacements"), ConfigManager.isLogReplacements())
                        .setDefaultValue(true)
                        .setTooltip(Text.literal("If enabled, logs how many blocks are replaced per chunk."))
                        .setSaveConsumer(ConfigManager::setLogReplacements)
                        .build()
        );

        optionsCat.addEntry(
                entryBuilder
                        .startBooleanToggle(Text.literal("Scan overworld on startup"), ConfigManager.isScanOverworldOnStartup())
                        .setDefaultValue(true)
                        .setTooltip(Text.literal("If enabled, scans loaded overworld chunks at server startup."))
                        .setSaveConsumer(ConfigManager::setScanOverworldOnStartup)
                        .build()
        );

        // MAPPINGS CATEGORY (two text fields per row, fully editable)
        ConfigCategory mappingsCat = builder.getOrCreateCategory(Text.literal("Mappings"));

        for (int i = 0; i < mappingEntries.size(); i++) {
            MappingEntry entry = mappingEntries.get(i);
            int index = i;

            mappingsCat.addEntry(
                    entryBuilder
                            .startStrField(Text.literal("Source block ID #" + (index + 1)), entry.key)
                            .setTooltip(Text.literal("Original block ID (e.g. minecraft:pale_oak_log)"))
                            .setSaveConsumer(newKey -> entry.key = newKey.trim())
                            .build()
            );

            mappingsCat.addEntry(
                    entryBuilder
                            .startStrField(Text.literal("Replacement block ID #" + (index + 1)), entry.value)
                            .setTooltip(Text.literal("Replacement block ID (e.g. minecraft:birch_log)"))
                            .setSaveConsumer(newVal -> entry.value = newVal.trim())
                            .build()
            );
        }

        // SAVE HANDLER
        builder.setSavingRunnable(() -> {
            try {
                // Rebuild mappings from edited entries
                Map<String, String> newMappings = new LinkedHashMap<>();
                for (MappingEntry e : mappingEntries) {
                    if (e.key != null && !e.key.isEmpty()) {
                        if (e.value != null && !e.value.isEmpty()) {
                            newMappings.put(e.key, e.value);
                        }
                    }
                }
                ConfigManager.setMappings(newMappings);
                ConfigManager.save();
            } catch (Exception e) {
                BlockSubMod.LOGGER.error("[BlockSub] Failed to save config from GUI", e);
            }
        });

        return builder.build();
    }
}
