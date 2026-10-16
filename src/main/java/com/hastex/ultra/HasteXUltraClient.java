package com.hastex.ultra;

import net.fabricmc.api.ClientModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class HasteXUltraClient implements ClientModInitializer {
    public static final String MOD_ID = "hastexultra";
    public static final Logger LOGGER = LoggerFactory.getLogger("Haste X Ultra");

    @Override
    public void onInitializeClient() {
        HasteXUltraConfig.load();
        LOGGER.info("Haste X Ultra loaded (autoSprint={}). Config: config/{}.json",
                HasteXUltraConfig.get().autoSprint, MOD_ID);
    }
}
