package com.zxese.blocksubstitutor;

import com.terraformersmc.modmenu.api.ModMenuApi;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;

import com.zxese.blocksubstitutor.config.ConfigManager;

import net.minecraft.client.gui.screen.Screen;

/**
 * Mod Menu integration for Block Substitutor.
 *
 * Provides a config screen factory that opens the custom mappings editor UI.
 */
public class BlockSubstitutorModMenu implements ModMenuApi {

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return (Screen parent) ->
                new BlockMappingsEditorScreen(parent, ConfigManager.getConfig());
    }
}