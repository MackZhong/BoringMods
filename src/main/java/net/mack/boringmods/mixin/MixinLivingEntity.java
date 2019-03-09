package net.mack.boringmods.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Position;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class MixinLivingEntity {
    private org.apache.logging.log4j.Logger logger = org.apache.logging.log4j.LogManager.getLogger("boringmods");

    @Inject(method = "method_16080", at = @At("HEAD"), cancellable = true)
    private void method_16080(DamageSource damageSource_1, CallbackInfo ci) {
        BlockPos pos = MinecraftClient.getInstance().player.getPos();
        logger.info("%s killed by %s at %d, %d, %d.",
                MinecraftClient.getInstance().player.getDisplayName().getFormattedText(),
                damageSource_1.getName(), pos.getX(), pos.getY(), pos.getZ());
    }
}
