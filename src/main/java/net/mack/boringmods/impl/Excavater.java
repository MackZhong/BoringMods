package net.mack.boringmods.impl;

import net.fabricmc.fabric.api.client.keybinding.FabricKeyBinding;
import net.fabricmc.fabric.api.client.keybinding.KeyBindingRegistry;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.Identifier;

public class Excavater {
    public static FabricKeyBinding keyExcavate;

    public static void keyBinding(String category) {
        keyExcavate = FabricKeyBinding.Builder.create(
                new Identifier("boringmods:excavate"),
                InputUtil.Type.KEY_KEYBOARD,
                96,
                category
        ).build();
        KeyBindingRegistry.INSTANCE.register(keyExcavate);
    }
}
