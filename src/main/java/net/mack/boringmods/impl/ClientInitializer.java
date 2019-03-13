package net.mack.boringmods.impl;


import net.fabricmc.fabric.api.client.keybinding.FabricKeyBinding;
import net.fabricmc.fabric.api.client.keybinding.KeyBindingRegistry;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.Identifier;

public class ClientInitializer implements net.fabricmc.api.ClientModInitializer {
    private org.apache.logging.log4j.Logger logger = org.apache.logging.log4j.LogManager.getLogger("boringmods");
    public static FabricKeyBinding keyExcavate;

    @Override
    public void onInitializeClient() {
        KeyBindingRegistry.INSTANCE.addCategory("boringmods.category");
        keyExcavate = FabricKeyBinding.Builder.create(
                new Identifier("boringmods:excavate"),
                InputUtil.Type.KEY_KEYBOARD,
                96,
                "boringmods.category"
        ).build();
        KeyBindingRegistry.INSTANCE.register(keyExcavate);

        logger.info("Boring Mods Client Initialization.");
    }
}
