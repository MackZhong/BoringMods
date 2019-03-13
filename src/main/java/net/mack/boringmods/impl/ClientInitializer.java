package net.mack.boringmods.impl;


import net.fabricmc.fabric.api.client.keybinding.FabricKeyBinding;
import net.fabricmc.fabric.api.client.keybinding.KeyBindingRegistry;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.Identifier;

public class ClientInitializer implements net.fabricmc.api.ClientModInitializer {
    private org.apache.logging.log4j.Logger logger = org.apache.logging.log4j.LogManager.getLogger("boringmods");
    public static FabricKeyBinding keybind;

    @Override
    public void onInitializeClient() {
        KeyBindingRegistry.INSTANCE.addCategory("boringmods.category");
        keybind = FabricKeyBinding.Builder.create(
                new Identifier("boringmods:excavate"),
                InputUtil.Type.KEY_KEYBOARD,
                96,
                "boringmods.category"
        ).build();
        KeyBindingRegistry.INSTANCE.register(keybind);

        logger.info("Boring Mods Client Initialization.");
    }
}
