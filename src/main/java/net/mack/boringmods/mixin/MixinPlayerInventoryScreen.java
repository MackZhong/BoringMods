package net.mack.boringmods.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.mack.boringmods.client.gui.button.SortButtonWidget;
import net.minecraft.client.gui.Screen;
import net.minecraft.client.gui.ingame.AbstractPlayerInventoryScreen;
import net.minecraft.client.gui.ingame.PlayerInventoryScreen;
import net.minecraft.client.gui.recipebook.RecipeBookGui;
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
public abstract class MixinPlayerInventoryScreen extends AbstractPlayerInventoryScreen<PlayerContainer> {
    private org.apache.logging.log4j.Logger logger = org.apache.logging.log4j.LogManager.getLogger("boringmods");

    @Shadow @Final private RecipeBookGui recipeBook;

    private MixinPlayerInventoryScreen(PlayerEntity playerEntity) {
        super(playerEntity.playerContainer, playerEntity.inventory, new TranslatableTextComponent("container.crafting"));

    }

    @Inject(method = "onInitialized"
            , at = @At(value = "INVOKE",
            args = "log=true",
            target = "Lnet/minecraft/client/gui/ingame/PlayerInventoryScreen;addButton(Lnet/minecraft/client/gui/widget/AbstractButtonWidget;)Lnet/minecraft/client/gui/widget/AbstractButtonWidget;"))
    private void onCreateButton(CallbackInfo callbackInfo) {
        logger.info(String.format("width: %d, height: %d, containerWidth: %d, containerHeight: %d", this.width, this.height, this.getContainer().getCraftingWidth(), this.getContainer().getCraftingHeight()));
        logger.info(String.format("ScaledWidth: %d, ScaledHeight: %d", this.client.window.getScaledWidth(), this.client.window.getScaledHeight()));
        logger.info(String.format("FramebufferWidth: %d, FramebufferHeight: %d", this.client.window.getFramebufferWidth(), this.client.window.getFramebufferHeight()));
        this.addButton(new SortButtonWidget(11,
                this.left + 144,
                this.top + this.height / 2 - 22,
                10,
                8,
                this.container,
                this.recipeBook));
    }
}
