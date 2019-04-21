package net.mack.boringmods.init;


import net.fabricmc.fabric.api.client.keybinding.KeyBindingRegistry;
import net.fabricmc.loader.api.FabricLoader;
import net.mack.boringmods.client.gui.menu.ModConfigsScreen;
import net.mack.boringmods.client.options.ModConfigs;
import net.mack.boringmods.impl.Excavator;
import net.mack.boringmods.impl.LightOverlay;
import net.mack.boringmods.impl.QuickInfo;

import java.lang.reflect.Method;
import java.util.concurrent.atomic.AtomicBoolean;

public class ClientMod implements net.fabricmc.api.ClientModInitializer {
    private org.apache.logging.log4j.Logger logger = org.apache.logging.log4j.LogManager.getLogger("boringmods");
    private final static String keyBindingCategory = "boringmods.category";
    private static AtomicBoolean modMenuLoaded = new AtomicBoolean(false);

    @Override
    public void onInitializeClient() {
        KeyBindingRegistry.INSTANCE.addCategory(keyBindingCategory);

        Excavator.getInstance().keyBinding(keyBindingCategory);
        LightOverlay.INSTANCE.keyBinding(keyBindingCategory);
        QuickInfo.INSTANCE.keyBinding(keyBindingCategory);

        if (FabricLoader.getInstance().isModLoaded("modmenu") && modMenuLoaded.compareAndSet(false, true)) {
            try {
                Class<?> clazz = Class.forName("io.github.prospector.modmenu.api.ModMenuApi");
                Method method = clazz.getMethod("addConfigOverride", String.class, Runnable.class);
                method.invoke(null, ModConfigs.MOD_ID, ModConfigs.INSTANCE);
                ModConfigs.menuAdded = true;
            } catch (Exception e) {
                ModConfigs.LOGGER.error("[BoringMods] Failed to add config override for ModMenu!", e);
            }
        }

        logger.info("Boring Mods Client Initialization.");
    }
}
