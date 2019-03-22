package net.mack.boringmods.init;

import net.mack.boringmods.impl.Excavator;
import net.mack.boringmods.impl.Harvest;

public class ModInitializer implements net.fabricmc.api.ModInitializer {
    private org.apache.logging.log4j.Logger logger = org.apache.logging.log4j.LogManager.getLogger("boringmods");

    @Override
    public void onInitialize() {
        Excavator.getInstance().registerServerSidePacket();
        Harvest.INSTANCE.onInitialize();

        logger.info("Boring Mods Initialization.");
    }
}
