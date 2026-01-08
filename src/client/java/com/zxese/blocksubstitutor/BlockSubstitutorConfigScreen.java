package com.zxese.blocksubstitutor;

import com.zxese.blocksubstitutor.config.BlockSubConfig;
import com.zxese.blocksubstitutor.config.ConfigManager;

import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

/**
 * BlockSubstitutorConfigScreen
 *
 * Provides a Cloth Config screen for editing the BlockSubstitutor config.
 */
public final class BlockSubstitutorConfigScreen {

    private BlockSubstitutorConfigScreen() {
        // Utility class; no instantiation.
    }

    /**
     * Creates a new Cloth Config screen for editing the config.
     *
     * @param parent The parent screen (Mod Menu passes this in)
     * @param config The config instance to edit
     * @return A new Screen instance
     */
    public static Screen create(Screen parent, BlockSubConfig config) {

        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(Text.literal("Block Substitutor Configuration"));

        ConfigEntryBuilder entryBuilder = builder.entryBuilder();

        // ---------------------------------------------------------
        // General Category
        // ---------------------------------------------------------
        ConfigCategory general = builder.getOrCreateCategory(
                Text.literal("General Settings")
        );

        general.addEntry(
                entryBuilder.startBooleanToggle(
                                Text.literal("Log Replacement Summary"),
                                config.isLogReplacements()
                        )
                        .setDefaultValue(true)
                        .setSaveConsumer(config::setLogReplacements)
                        .build()
        );

        // ---------------------------------------------------------
        // Save Action
        // ---------------------------------------------------------
        builder.setSavingRunnable(() -> {
            ConfigManager.save(
                    FabricLoader.getInstance().getConfigDir()
            );
        });

        return builder.build();
    }
}