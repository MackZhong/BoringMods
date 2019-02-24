package net.mack.boringmods.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BoundingBox;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

//@Environment(EnvType.CLIENT)
@Mixin(value = PlayerEntity.class)
public abstract class MixinPlayerEntity extends LivingEntity {
    private org.apache.logging.log4j.Logger logger = org.apache.logging.log4j.LogManager.getLogger("boringmods");

    protected MixinPlayerEntity(EntityType<? extends LivingEntity> entityType_1, World world_1) {
        super(entityType_1, world_1);
    }

    @Redirect(method = "updateMovement",
            at = @At(
                    value = "INVOKE"
//                    , args = "log=true"
                    , ordinal = 1
                    , target = "Lnet/minecraft/util/math/BoundingBox;expand(DDD)Lnet/minecraft/util/math/BoundingBox;"
                    // net.minecraft.entity.Entity.getBoundingBox
            ))
    private BoundingBox onExpand(BoundingBox this$Box, double x, double y, double z) {
//        this.logger.info(String.format("expand(%f, %f, %f).", x, y, z));

        return this$Box.expand(x + 7, y + 7, z + 7);
    }

    @Override
    public boolean canBreatheInWater() {
       return true;
    }

}
