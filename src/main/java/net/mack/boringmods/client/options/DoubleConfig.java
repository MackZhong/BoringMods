package net.mack.boringmods.client.options;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.mack.boringmods.client.gui.ModConfigSliderWidget;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.util.math.MathHelper;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

@Environment(EnvType.CLIENT)
public class DoubleConfig extends Config {
    private final float stepValue;
    private final double lowLimit;
    private double highLimit;
    private final Function<ModConfigs, Double> func;
    private final BiConsumer<ModConfigs, Double> biConsumer;
    private final BiFunction<ModConfigs, DoubleConfig, String> biFunction;

    DoubleConfig(String optionKey, double low, double high, float step, Function<ModConfigs, Double> func1, BiConsumer<ModConfigs, Double> bi1, BiFunction<ModConfigs, DoubleConfig, String> func2) {
        super(optionKey);
        this.lowLimit = low;
        this.highLimit = high;
        this.stepValue = step;
        this.func = func1;
        this.biConsumer = bi1;
        this.biFunction = func2;
    }

    @Override
    public AbstractButtonWidget createOptionButton(ModConfigs options, int x, int y, int width) {
        return new ModConfigSliderWidget(options, x, y, width,20, this);
    }

    public double toPercent(double double_1) {
        return MathHelper.clamp((this.roundValue(double_1) - this.lowLimit) / (this.highLimit - this.lowLimit), 0.0D, 1.0D);
    }

    public double fromPercent(double double_1) {
        return this.roundValue(MathHelper.lerp(MathHelper.clamp(double_1, 0.0D, 1.0D), this.lowLimit, this.highLimit));
    }

    private double roundValue(double double_1) {
        if (this.stepValue > 0.0F) {
            double_1 = (double)(this.stepValue * (float)Math.round(double_1 / (double)this.stepValue));
        }

        return MathHelper.clamp(double_1, this.lowLimit, this.highLimit);
    }

    public double getLow() {
        return this.lowLimit;
    }

    public double getHigh() {
        return this.highLimit;
    }

    public void setHigh(float float_1) {
        this.highLimit = (double)float_1;
    }

    public void setValue(ModConfigs gameOptions_1, double double_1) {
        this.biConsumer.accept(gameOptions_1, double_1);
    }

    public Double getValue(ModConfigs gameOptions_1) {
        return this.func.apply(gameOptions_1);
    }

    public String getValueString(ModConfigs gameOptions_1) {
        return this.biFunction.apply(gameOptions_1, this);
    }

}
