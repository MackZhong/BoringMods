package net.mack.boringmods.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.mack.boringmods.client.gui.hud.QuickInfoHud;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.InGameHud;
import org.spongepowered.asm.lib.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(value = InGameHud.class)
public abstract class MixinInGameHud {
    private org.apache.logging.log4j.Logger logger = org.apache.logging.log4j.LogManager.getLogger("boringmods");

    private QuickInfoHud hudInfo;

    @Shadow
    @Final
    private MinecraftClient client;

    @Inject(method = "<init>(Lnet/minecraft/client/MinecraftClient;)V"
            , at = @At(
            value = "RETURN"
    ))
    private void onInit(MinecraftClient mcClient, CallbackInfo ci) {
        this.logger.info("InGameHud initialization, and Info HUD.");
        this.hudInfo = new QuickInfoHud(mcClient);
    }

    @Inject(method = "draw"
            , at = @At(
            value = "FIELD"
            , target = "Lnet/minecraft/client/options/GameOptions;hudHidden:Z"
            , opcode = Opcodes.GETFIELD
//            , args = "log=true"
            , ordinal = 2
    ))
    private void onDraw(float esp, CallbackInfo ci) {
        if (!this.client.options.debugEnabled) {
            this.hudInfo.draw();
        }
    }

    @Inject(method = "method_1745"
            , at = @At(
            value = "RETURN"
    ))
    private void onReset(CallbackInfo ci) {
       // this.hudInfo.resetChunk();
        // this.logger.info("OnReset");
    }
}
