package net.mack.boringmods.init;


import net.fabricmc.fabric.api.client.keybinding.KeyBindingRegistry;
import net.fabricmc.fabric.impl.client.keybinding.KeyBindingRegistryImpl;
import net.mack.boringmods.impl.Excavator;
import net.mack.boringmods.impl.LightOverlay;

public class ClientInitializer implements net.fabricmc.api.ClientModInitializer {
    private org.apache.logging.log4j.Logger logger = org.apache.logging.log4j.LogManager.getLogger("boringmods");
    private final static String keyBindingCategory = "boringmods.category";

    @Override
    public void onInitializeClient() {
        KeyBindingRegistry.INSTANCE.addCategory(keyBindingCategory);

        Excavator.getInstance().keyBinding(keyBindingCategory);
        KeyBindingRegistryImpl.INSTANCE.register(LightOverlay.getInstance().getKeyBinding(keyBindingCategory));

        logger.info("Boring Mods Client Initialization.");
    }
}
