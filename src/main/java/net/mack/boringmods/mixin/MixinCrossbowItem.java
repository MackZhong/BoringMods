package net.mack.boringmods.mixin;

import com.google.common.collect.Lists;
import net.mack.boringmods.client.options.ModOptions;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;

@Mixin(CrossbowItem.class)
public abstract class MixinCrossbowItem {
    @Redirect(
            method = "buildTooltip",
            at = @At(
                    value = "INVOKE",
//                    args = "log=true",
                    target = "Lnet/minecraft/item/CrossbowItem;getChargedProjectiles(Lnet/minecraft/item/ItemStack;)Ljava/util/List;"
            )
    )
    private List<ItemStack> getChargedProjectiles(ItemStack itemStack_1) {
        List<ItemStack> list_1 = Lists.newArrayList();
        CompoundTag compoundTag_1 = itemStack_1.getTag();
        ListTag listTag_1 = null;
        if (null == compoundTag_1) {
            compoundTag_1 = new CompoundTag();
            ModOptions.LOGGER.error("[BoringMods]This CrossbowItem doesn't have compound tag.");
        }
        if (compoundTag_1.containsKey("ChargedProjectiles", 9)) {
            listTag_1 = compoundTag_1.getList("ChargedProjectiles", 10);
        } else {
            listTag_1 = new ListTag();
            ModOptions.LOGGER.error("[BoringMods]This CrossbowItem's compound tag doesn't contain \"ChargedProjectiles\" key.");
        }
        if (0 == listTag_1.size()) {
            CompoundTag projectile = new CompoundTag();
            projectile.putString("id", "minecraft:arrow");
            projectile.putByte("Count", (byte) 1);
            listTag_1.addTag(0, projectile);
            compoundTag_1.put("ChargedProjectiles", listTag_1);
            itemStack_1.setTag(compoundTag_1);

            ModOptions.LOGGER.warn("[BoringMods]\"ChargedProjectiles\" key added.");
            list_1.add(ItemStack.fromTag(projectile));
            return list_1;
        }

        for (int int_1 = 0; int_1 < listTag_1.size(); ++int_1) {
            CompoundTag compoundTag_2 = listTag_1.getCompoundTag(int_1);
            list_1.add(ItemStack.fromTag(compoundTag_2));
        }

        if (0 == list_1.size()) {
            ModOptions.LOGGER.error("[BoringMods]ItemStack is empty.");
        }

        return list_1;
    }
}
