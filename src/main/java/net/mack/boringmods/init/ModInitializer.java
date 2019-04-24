package net.mack.boringmods.init;

import net.mack.boringmods.impl.Excavator;

public class ModInitializer implements net.fabricmc.api.ModInitializer {
    private org.apache.logging.log4j.Logger logger = org.apache.logging.log4j.LogManager.getLogger("boringmods");

    @Override
    public void onInitialize() {
        Excavator.INSTANCE.registerServerSidePacket();

        logger.info("Boring Mods Initialization.");
    }
}
