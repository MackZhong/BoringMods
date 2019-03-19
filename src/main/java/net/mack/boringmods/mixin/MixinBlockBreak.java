package net.mack.boringmods.mixin;

import net.mack.boringmods.impl.Excavator;
import net.minecraft.block.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Block.class)
public abstract class MixinBlockBreak {
    private org.apache.logging.log4j.Logger logger = org.apache.logging.log4j.LogManager.getLogger("boringmods");

    @Inject(method = "onBreak",
            at = @At(value = "HEAD"))
    private void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player, CallbackInfo ci) {
        if (Excavator.getInstance().isEnable() &&
                world.isClient() &&
                player.isUsingEffectiveTool(state) &&
                player.getHungerManager().getFoodLevel() > 0) {
            Excavator.getInstance().excavate(world, pos, state, player);
        }
    }
}
