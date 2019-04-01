package net.mack.boringmods.mixin;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ProjectileEntity.class)
public class MixinArrowEntity {
    @Shadow public ProjectileEntity.PickupType pickupType;

    @Inject(method = "<init>(Lnet/minecraft/entity/EntityType;Lnet/minecraft/world/World;)V",
    at= @At(value = "RETURN"))
    private void onInit(EntityType projectileType, World world, CallbackInfo ci){
        this.pickupType = ProjectileEntity.PickupType.PICKUP;
    }

    @Inject(method = "<init>(Lnet/minecraft/entity/EntityType;Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/world/World;)V",
            at= @At(value = "RETURN"))
    private void onInit(EntityType projectileType, LivingEntity playerEntiy, World world, CallbackInfo ci){
        this.pickupType = ((PlayerEntity)playerEntiy).abilities.creativeMode ? ProjectileEntity.PickupType.CREATIVE_PICKUP : ProjectileEntity.PickupType.PICKUP;
    }

    @Inject(method = "<init>(Lnet/minecraft/entity/EntityType;DDDLnet/minecraft/world/World;)V",
            at= @At(value = "RETURN"))
    private void onInit(EntityType projectileType,double x, double y, double z, World world, CallbackInfo ci){
        this.pickupType = ProjectileEntity.PickupType.PICKUP;
    }
}
