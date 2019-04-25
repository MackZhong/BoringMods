package net.mack.boringmods.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.mack.boringmods.client.gui.menu.ModConfigsScreen;
import net.mack.boringmods.client.options.ModConfigs;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.MainMenuScreen;
import net.minecraft.client.gui.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.TextComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(MainMenuScreen.class)
public abstract class MixinMainMenuScreen extends Screen {
    protected MixinMainMenuScreen(TextComponent textComponent_1) {
        super(textComponent_1);
    }

    @Inject(at = @At("RETURN"), method = "initWidgetsNormal(II)V")
    public void addConfigMenuButton(CallbackInfo info) {
        if (!ModConfigs.INSTANCE.menuAdded) {
            this.addButton(new ButtonWidget(
                    this.width / 2 - 100,
                    this.height / 4 + 24,
                    200,
                    20,
                    I18n.translate("boringmods.configs.title"),
                    buttonWidget -> MinecraftClient.getInstance().openScreen(new ModConfigsScreen(this, ModConfigs.INSTANCE))));
        }
    }
}
