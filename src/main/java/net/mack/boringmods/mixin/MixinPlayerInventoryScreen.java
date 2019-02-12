package net.mack.boringmods.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.mack.boringmods.client.gui.button.SortButtonWidget;
import net.minecraft.client.gui.Screen;
import net.minecraft.client.gui.ingame.AbstractPlayerInventoryScreen;
import net.minecraft.client.gui.ingame.PlayerInventoryScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.RecipeBookButtonWidget;
import net.minecraft.container.ContainerType;
import net.minecraft.container.GenericContainer;
import net.minecraft.container.PlayerContainer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Environment(EnvType.CLIENT)
@Mixin(value = PlayerInventoryScreen.class)
public abstract class MixinPlayerInventoryScreen extends AbstractPlayerInventoryScreen<PlayerContainer> {
    private static final Identifier SORT_BUTTON_TEX = new Identifier("boringmods:textures/gui/sort_button.png");
//    private static final Identifier RECIPE_BUTTON_TEX = new Identifier("textures/gui/recipe_button.png");

    private MixinPlayerInventoryScreen(PlayerEntity playerEntity) {
        super(playerEntity.containerPlayer, playerEntity.inventory, new TranslatableTextComponent("container.crafting"));

    }

    @Inject(method = "onInitialized"
            , at = @At(value = "INVOKE",
//            args = "log=true",
            target = "Lnet/minecraft/client/gui/ingame/PlayerInventoryScreen;addButton(Lnet/minecraft/client/gui/widget/ButtonWidget;)Lnet/minecraft/client/gui/widget/ButtonWidget;"))
    private void onCreateButton(CallbackInfo callbackInfo) {
        this.addButton(new SortButtonWidget(11,
                this.left + 144,
                this.height / 2 - 22,
                20,
                18,
                0,
                0,
                19,
                SORT_BUTTON_TEX,
                this.container,
                this.left,
                this.top));
    }

}
