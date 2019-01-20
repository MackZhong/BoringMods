package net.mack.boringmods.mixin;

import net.minecraft.server.network.ServerPlayerInteractionManager;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value = ServerPlayerInteractionManager.class)
public abstract class MixinServerPlayerInteractionManager {
}
