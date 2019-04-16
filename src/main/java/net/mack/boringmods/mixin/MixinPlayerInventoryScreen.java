package net.mack.boringmods.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.mack.boringmods.client.gui.button.SortButtonWidget;
import net.minecraft.client.gui.ingame.AbstractPlayerInventoryScreen;
import net.minecraft.client.gui.ingame.PlayerInventoryScreen;
import net.minecraft.client.gui.recipebook.RecipeBookGui;
import net.minecraft.container.Container;
import net.minecraft.container.PlayerContainer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.TextComponent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Deprecated
@Environment(EnvType.CLIENT)
@Mixin(value = PlayerInventoryScreen.class)
public abstract class MixinPlayerInventoryScreen extends AbstractPlayerInventoryScreen<PlayerContainer> {
    private org.apache.logging.log4j.Logger logger = org.apache.logging.log4j.LogManager.getLogger("boringmods");

    @Shadow
    @Final
    private RecipeBookGui recipeBook;

    public MixinPlayerInventoryScreen(PlayerContainer container_1, PlayerInventory playerInventory_1, TextComponent textComponent_1) {
        super(container_1, playerInventory_1, textComponent_1);
    }

    @Inject(method = "init"
            , remap = false
            , at = @At(value = "INVOKE"
//            args = "log=true",Lnet/minecraft/client/gui/ingame/PlayerInventoryScreen;<init>(Lnet/minecraft/entity/player/PlayerEntity;)V
            , target = "Lnet/minecraft/client/gui/Screen;addButton(Lnet/minecraft/client/gui/widget/AbstractButtonWidget;)Lnet/minecraft/client/gui/widget/AbstractButtonWidget;"
    ))
    private void addButton(CallbackInfo callbackInfo) {
        PlayerContainer playerContainer = (PlayerContainer) this.container;
        logger.info(String.format("width: %d, height: %d, containerWidth: %d, containerHeight: %d", this.width, this.height, playerContainer.getCraftingWidth(), playerContainer.getCraftingHeight()));
        logger.info(String.format("ScaledWidth: %d, ScaledHeight: %d", this.minecraft.window.getScaledWidth(), this.minecraft.window.getScaledHeight()));
        logger.info(String.format("FramebufferWidth: %d, FramebufferHeight: %d", this.minecraft.window.getFramebufferWidth(), this.minecraft.window.getFramebufferHeight()));
        this.addButton(new SortButtonWidget(11,
                this.left + 144,
                this.top + this.height / 2 - 22,
                10,
                8,
                this.container,
                this.recipeBook,
                (buttonWidget_1) -> {
                    logger.info("Sort button press.");
                }));
    }

    @Override
    public boolean mouseScrolled(double double_1, double double_2, double double_3) {
        if (super.mouseScrolled(double_1, double_2, double_3))
            return true;
        return false;//((IRecipeBookGui) recipeBook).mouseWheelie_scroll(double_1, double_2, double_3);
    }
}
