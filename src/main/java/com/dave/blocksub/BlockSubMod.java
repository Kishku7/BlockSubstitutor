package com.dave.blocksub;

import net.fabricmc.api.ModInitializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class BlockSubMod implements ModInitializer {
    public static final String MOD_ID = "block_substitutor";
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        BlockMapping.load();
        ChunkLoadHandler.register();
        StartupWorldScanner.register();

        LOGGER.info("[BlockSub] Initialized with {} substitution rules.",
                BlockMapping.getMappings().size());
    }
}
