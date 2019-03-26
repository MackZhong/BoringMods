package net.mack.boringmods.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.mack.boringmods.client.gui.button.SortButtonWidget;
import net.mack.boringmods.util.IRecipeBookGui;
import net.minecraft.client.gui.Screen;
import net.minecraft.client.gui.ingame.AbstractPlayerInventoryScreen;
import net.minecraft.client.gui.ingame.PlayerInventoryScreen;
import net.minecraft.client.gui.recipebook.RecipeBookGui;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.RecipeBookButtonWidget;
import net.minecraft.container.Container;
import net.minecraft.container.ContainerType;
import net.minecraft.container.GenericContainer;
import net.minecraft.container.PlayerContainer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Environment(EnvType.CLIENT)
@Mixin(value = PlayerInventoryScreen.class)
public abstract class MixinPlayerInventoryScreen extends AbstractPlayerInventoryScreen {
    private org.apache.logging.log4j.Logger logger = org.apache.logging.log4j.LogManager.getLogger("boringmods");

    @Shadow
    @Final
    private RecipeBookGui recipeBook;

    public MixinPlayerInventoryScreen(Container container_1, PlayerInventory playerInventory_1, TextComponent textComponent_1) {
        super(container_1, playerInventory_1, textComponent_1);
    }

    @Inject(method = "init()V"
            , at = @At(value = "INVOKE",
            args = "log=true",
            target = "Lnet/minecraft/client/gui/ingame/PlayerInventoryScreen;focusOn(Lnet/minecraft/client/gui/InputListener;)V"
            //"Lnet/minecraft/client/gui/Screen;addButton(Lnet/minecraft/client/gui/widget/AbstractButtonWidget;)Lnet/minecraft/client/gui/widget/AbstractButtonWidget;"
            //"Lnet/minecraft/client/gui/ingame/PlayerInventoryScreen;addButton(Lnet/minecraft/client/gui/widget/AbstractButtonWidget;)Lnet/minecraft/client/gui/widget/AbstractButtonWidget;"
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
        return  false;//((IRecipeBookGui) recipeBook).mouseWheelie_scroll(double_1, double_2, double_3);
    }
}
