package net.mack.boringmods.init;


import net.fabricmc.fabric.api.client.keybinding.KeyBindingRegistry;
import net.fabricmc.fabric.impl.client.keybinding.KeyBindingRegistryImpl;
import net.fabricmc.loader.api.FabricLoader;
import net.mack.boringmods.client.options.ModOptions;
import net.mack.boringmods.impl.Excavator;
import net.mack.boringmods.impl.LightOverlay;

import java.lang.reflect.Method;
import java.util.concurrent.atomic.AtomicBoolean;

public class ClientInitializer implements net.fabricmc.api.ClientModInitializer {
    private org.apache.logging.log4j.Logger logger = org.apache.logging.log4j.LogManager.getLogger("boringmods");
    private final static String keyBindingCategory = "boringmods.category";
    public static AtomicBoolean modMenuLoaded = new AtomicBoolean(false);

    @Override
    public void onInitializeClient() {
        KeyBindingRegistry.INSTANCE.addCategory(keyBindingCategory);

        Excavator.getInstance().keyBinding(keyBindingCategory);
        KeyBindingRegistryImpl.INSTANCE.register(LightOverlay.getInstance().getKeyBinding(keyBindingCategory));

        if (FabricLoader.getInstance().isModLoaded("modmenu") && modMenuLoaded.compareAndSet(false, true)) {
            try {
                Class<?> clazz = Class.forName("io.github.prospector.modmenu.api.ModMenuApi");
                Method method = clazz.getMethod("addConfigOverride", String.class, Runnable.class);
                method.invoke(null, ModOptions.MOD_ID, ModOptions.INSTANCE);
                ModOptions.INSTANCE.menuAdded = true;
            } catch (Exception e) {
                ModOptions.LOGGER.error("[BoringMods] Failed to add config override for ModMenu!", e);
            }
        }

        logger.info("Boring Mods Client Initialization.");
    }
}
