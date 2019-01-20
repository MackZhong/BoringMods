package net.mack.boringmods.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.mack.boringmods.client.gui.hud.QuickInfoHud;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.DebugHud;
import net.minecraft.client.gui.hud.InGameHud;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Environment(EnvType.CLIENT)
@Mixin(value = InGameHud.class)
public abstract class MixinIngameHud {
    private org.apache.logging.log4j.Logger logger = org.apache.logging.log4j.LogManager.getLogger("BoringMods");

    private QuickInfoHud hudInfo;

    @Shadow @Final private MinecraftClient client;

   private void onInit(MinecraftClient mcClient){
       this.hudInfo = new QuickInfoHud(mcClient);
   }

    public void onDraw(float esp){
        if(!this.client.options.debugEnabled){
            this.hudInfo.draw(esp);
        }
    }

    private void onReset(){
       this.hudInfo.resetChunk();
    }
}
