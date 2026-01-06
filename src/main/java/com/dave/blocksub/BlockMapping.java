package com.dave.blocksub;

import net.minecraft.util.Identifier;
import net.minecraft.util.InvalidIdentifierException;

import java.util.HashMap;
import java.util.Map;

public class BlockMapping {

    public static void load() {
        ConfigManager.load();

        Map<String, String> validated = new HashMap<>();
        for (Map.Entry<String, String> e : ConfigManager.getMappings().entrySet()) {
            try {
                new Identifier(e.getKey());
                new Identifier(e.getValue());
                validated.put(e.getKey(), e.getValue());
            } catch (InvalidIdentifierException ex) {
                BlockSubMod.LOGGER.warn("[BlockSub] Invalid identifier in config: {} -> {}",
                        e.getKey(), e.getValue());
            }
        }

        ConfigManager.setMappings(validated);

        BlockSubMod.LOGGER.info("[BlockSub] Validated {} block mappings.", validated.size());
    }

    public static Map<String, String> getMappings() {
        return ConfigManager.getMappings();
    }

    public static String getReplacement(String originalId) {
        return ConfigManager.getMappings().get(originalId);
    }
}
