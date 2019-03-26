package net.mack.boringmods.mixin;

import net.mack.boringmods.client.gui.menu.ModSettingsScreen;
import net.mack.boringmods.client.options.ModOptions;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Screen;
import net.minecraft.client.gui.menu.PauseMenuScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.TextComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PauseMenuScreen.class)
public abstract class MixinPauseMenuScreen extends Screen {
    protected MixinPauseMenuScreen(TextComponent textComponent_1) {
        super(textComponent_1);
    }

    @Inject(at = @At("RETURN"), method = "onInitialized()V")
    public void drawMenuButton(CallbackInfo info) {
        if (!ModOptions.INSTANCE.menuAdded) {
            this.addButton(new ButtonWidget(
                    this.screenWidth / 2 - 102,
                    this.screenHeight / 4 - 16,
                    204,
                    20,
                    I18n.translate("boringmods.configs.title"),
                    buttonWidget -> MinecraftClient.getInstance().openScreen(new ModSettingsScreen(this, ModOptions.INSTANCE))));
        }
    }
}
