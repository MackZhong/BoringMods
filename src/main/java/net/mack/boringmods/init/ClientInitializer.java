package net.mack.boringmods.init;


import net.fabricmc.fabric.api.client.keybinding.KeyBindingRegistry;
import net.mack.boringmods.impl.Excavator;
import net.mack.boringmods.impl.LightOverlay;

public class ClientInitializer implements net.fabricmc.api.ClientModInitializer {
    private org.apache.logging.log4j.Logger logger = org.apache.logging.log4j.LogManager.getLogger("boringmods");
    private final String keyBindingCategory = "boringmods.category";

    @Override
    public void onInitializeClient() {
        KeyBindingRegistry.INSTANCE.addCategory(keyBindingCategory);

        Excavator.getInstance().keyBinding(keyBindingCategory);
        LightOverlay.keyBinding(keyBindingCategory);

        logger.info("Boring Mods Client Initialization.");
    }
}
