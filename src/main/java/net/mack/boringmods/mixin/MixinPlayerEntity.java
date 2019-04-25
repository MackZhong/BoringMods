package net.mack.boringmods.mixin;

import net.mack.boringmods.client.options.ModConfigs;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BoundingBox;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

//@Environment(EnvType.SERVER)
//@Environment(EnvType.CLIENT)
@Mixin(value = PlayerEntity.class)
public abstract class MixinPlayerEntity extends LivingEntity {
    protected MixinPlayerEntity(EntityType<? extends LivingEntity> entityType_1, World world_1) {
        super(entityType_1, world_1);
    }

    //
    // pickup distance
    //
    @Redirect(method = "updateState",
            at = @At(
                    value = "INVOKE"
//                    , args = "log=true"
                    , ordinal = 1
                    , target = "Lnet/minecraft/util/math/BoundingBox;expand(DDD)Lnet/minecraft/util/math/BoundingBox;"
                    // net.minecraft.entity.Entity.getBoundingBox
            ))
    private BoundingBox onUpdateMovement(BoundingBox this$Box, double x, double y, double z) {
        return this$Box.expand(ModConfigs.INSTANCE.pickupDistance - 1);
    }

    @Inject(method = "onDeath", at = @At("HEAD"), cancellable = true)
    private void onDeath(DamageSource damageSource_1, CallbackInfo ci) {
        Vec3d pos = this.getPos();
        ModConfigs.LOGGER.info(String.format("[BoringMods]%s killed by %s at %d, %d, %d.",
                this.getDisplayName().getFormattedText(),
                damageSource_1.getName(), (int)pos.getX(), (int)pos.getY(), (int)pos.getZ()));
    }

    //
    // breath in water
    //
//
//    @Override
//    public boolean canBreatheInWater() {
//       return true;
//    }

}
