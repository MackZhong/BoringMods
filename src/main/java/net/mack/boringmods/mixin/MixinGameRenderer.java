package net.mack.boringmods.mixin;

import net.mack.boringmods.impl.LightOverlay;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.GameRenderer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class MixinGameRenderer {
    @Shadow
    @Final
    private MinecraftClient client;

    @Inject(method = "renderCenter", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/debug/DebugRenderer;shouldRender()Z", ordinal = 0))
    public void renderLightOverlay(float delta, long long_1, CallbackInfo callbackInfo) {
        LightOverlay.INSTANCE.render(this.client.world, this.client.player);
    }
}
