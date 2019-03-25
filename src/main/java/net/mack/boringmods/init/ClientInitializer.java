package net.mack.boringmods.init;


import io.github.prospector.modmenu.api.ModMenuApi;
import net.fabricmc.fabric.api.client.keybinding.KeyBindingRegistry;
import net.fabricmc.fabric.impl.client.keybinding.KeyBindingRegistryImpl;
import net.mack.boringmods.client.options.ModOptions;
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

        ModMenuApi.addConfigOverride(ModOptions.MOD_ID, new ModOptions());

        logger.info("Boring Mods Client Initialization.");
    }
}
