package net.mack.mousewheelie.client.mixin;

import net.mack.mousewheelie.util.IContainerScreen;
import net.mack.mousewheelie.util.IScrollableRecipeBook;
import net.mack.mousewheelie.util.ISpecialScrollableScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.Mouse;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(Mouse.class)
public class MixinMouse {

    @Shadow
    @Final
    private MinecraftClient client;

    @Shadow
    private double x;

    @Shadow
    private double y;

    // Thanks to Danielshe Lnet/minecraft/client/gui/ParentElement;mouseScrolled(DDD)Z
    @Inject(method = "onMouseScroll(JDD)V"
            , at = @At(value = "INVOKE"
            , target = "Lnet/minecraft/client/gui/Screen;mouseScrolled(DDD)Z"
            , args = "log=true"
//            , remap = false
            , ordinal = 0)
            , cancellable = true
            , locals = LocalCapture.CAPTURE_FAILSOFT)
    public void onMouseScrolled(long windowHandle, double double_1, double scroll, CallbackInfo callbackInfo) {
        double mouseX = this.x * (double) this.client.window.getScaledWidth() / (double) this.client.window.getWidth();
        double mouseY = this.y * (double) this.client.window.getScaledHeight() / (double) this.client.window.getHeight();
        double scrollAmount = scroll * this.client.options.mouseWheelSensitivity;
        if (this.client.currentScreen instanceof IContainerScreen) {
            if (((IContainerScreen) this.client.currentScreen).mouseWheelie_onMouseScroll(mouseX, mouseY, scrollAmount)) {
                callbackInfo.cancel();
                return;
            }
        }
        if (this.client.currentScreen instanceof IScrollableRecipeBook) {
            if (((IScrollableRecipeBook) this.client.currentScreen).mouseWheelie_onMouseScrollRecipeBook(mouseX, mouseY, scrollAmount)) {
                callbackInfo.cancel();
                return;
            }
        }
        if (this.client.currentScreen instanceof ISpecialScrollableScreen) {
            if (((ISpecialScrollableScreen) this.client.currentScreen).mouseWheelie_onMouseScrolledSpecial(mouseX, mouseY, scrollAmount)) {
                callbackInfo.cancel();
                return;
            }
        }
    }
}
