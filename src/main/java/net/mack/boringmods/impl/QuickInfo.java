package net.mack.boringmods.impl;

import net.fabricmc.fabric.api.client.keybinding.FabricKeyBinding;
import net.fabricmc.fabric.impl.client.keybinding.KeyBindingRegistryImpl;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.Identifier;

public class QuickInfo {
    public final static QuickInfo INSTANCE = new QuickInfo();

    private FabricKeyBinding keyDetail;

    public boolean keyBinding(String category) {
        this.keyDetail = FabricKeyBinding.Builder.create(
                new Identifier("quickinfo:detail_info"),
                InputUtil.Type.KEYSYM,
                344,
                category
        ).build();
        KeyBindingRegistryImpl.INSTANCE.register(this.keyDetail);
        return true;
    }

    public boolean detail() {
        return this.keyDetail.isPressed();
    }
}
