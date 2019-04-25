package net.mack.boringmods.mixin;

import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;

@Deprecated
@Mixin(LivingEntity.class)
public abstract class MixinLivingEntity {
    private org.apache.logging.log4j.Logger logger = org.apache.logging.log4j.LogManager.getLogger("boringmods");

}
