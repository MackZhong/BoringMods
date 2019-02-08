package net.mack.boringmods.impl;


public class ClientInitializer implements net.fabricmc.api.ClientModInitializer {
    private org.apache.logging.log4j.Logger logger = org.apache.logging.log4j.LogManager.getLogger("boringmods");

    @Override
    public void onInitializeClient() {
        logger.info("Boring Mods Client Initialization.");
    }
}
