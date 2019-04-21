package net.mack.boringmods.mixin;

import net.mack.boringmods.impl.LightOverlay;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public abstract class MixinMinecraftClient {

    @Inject(method = "handleInputEvents()V", at = @At("RETURN"))
    private void handleInputEvents(CallbackInfo callbackInfo) {
        LightOverlay.INSTANCE.handleInput();
    }
}
