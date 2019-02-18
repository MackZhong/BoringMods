package net.mack.boringmods.impl;

public class ModInitializer implements net.fabricmc.api.ModInitializer {
    private org.apache.logging.log4j.Logger logger = org.apache.logging.log4j.LogManager.getLogger("boringmods");

    @Override
    public void onInitialize() {
        logger.info("Boring Mods Initialization.");
    }
}
