package net.mack.boringmods.client.gui;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.mack.boringmods.client.options.DoubleModOption;
import net.mack.boringmods.client.options.ModOptions;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.audio.SoundManager;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class ModOptionSliderWidget extends AbstractButtonWidget {
    private final ModOptions options;
    private final DoubleModOption option;
    protected double progress;

    public ModOptionSliderWidget(ModOptions options, int x, int y, int width, int height, DoubleModOption modOption) {
        super(x, y, width, height, "");
        this.progress = modOption.toPercent(modOption.getValue(options));
        this.options = options;
        this.option = modOption;
        this.updateText();
    }

    protected int getYImage(boolean boolean_1) {
        return 0;
    }

    protected String getNarrationMessage() {
        return I18n.translate("gui.narrate.slider", new Object[]{this.getMessage()});
    }

    protected void renderBg(MinecraftClient minecraftClient_1, int int_1, int int_2) {
        minecraftClient_1.getTextureManager().bindTexture(WIDGETS_LOCATION);
        GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.blit(this.x + (int)(this.progress * (double)(this.width - 8)), this.y, 0, 66, 4, 20);
        this.blit(this.x + (int)(this.progress * (double)(this.width - 8)) + 4, this.y, 196, 66, 4, 20);
    }

    public boolean mouseClicked(double double_1, double double_2, int int_1) {
        if (int_1 == 0) {
            boolean boolean_1 = this.clicked(double_1, double_2);
            if (boolean_1) {
                this.playDownSound(MinecraftClient.getInstance().getSoundManager());
                this.changeProgress(double_1);
                return true;
            }
        }

        return false;
    }

    public boolean keyPressed(int int_1, int int_2, int int_3) {
        double double_3;
        double double_4;
        if (int_1 == 263) {
            double_3 = this.progress;
            double_4 = 1.0D / (double)(this.width - 8);
            this.progress = MathHelper.clamp(this.progress - double_4, 0.0D, 1.0D);
            if (double_3 != this.progress) {
                this.onProgressChanged();
            }

            this.updateText();
        } else if (int_1 == 262) {
            double_3 = this.progress;
            double_4 = 1.0D / (double)(this.width - 8);
            this.progress = MathHelper.clamp(this.progress + double_4, 0.0D, 1.0D);
            if (double_3 != this.progress) {
                this.onProgressChanged();
            }

            this.updateText();
        }

        return false;
    }

    private void changeProgress(double double_1) {
        double double_2 = this.progress;
        this.progress = MathHelper.clamp((double_1 - (double)(this.x + 4)) / (double)(this.width - 8), 0.0D, 1.0D);
        if (double_2 != this.progress) {
            this.onProgressChanged();
        }

        this.updateText();
    }

    protected void onDrag(double double_1, double double_2, double double_3, double double_4) {
        this.changeProgress(double_1);
        super.onDrag(double_1, double_2, double_3, double_4);
    }

    public void playDownSound(SoundManager soundManager_1) {
    }

    public void onRelease(double double_1, double double_2) {
        super.playDownSound(MinecraftClient.getInstance().getSoundManager());
    }

    protected void onProgressChanged() {
        this.option.setValue(this.options, this.option.fromPercent(this.progress));
        this.options.write();
    }

    protected void updateText()    {
        this.setMessage(this.option.getValueString(this.options));
    }

}
