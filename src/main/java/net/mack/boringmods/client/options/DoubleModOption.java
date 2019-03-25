package net.mack.boringmods.client.options;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.mack.boringmods.client.gui.ModOptionSliderWidget;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.util.math.MathHelper;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

@Environment(EnvType.CLIENT)
public class DoubleModOption extends ModOption {
    private final float value;
    private final double lowLimit;
    private double highLimit;
    private final Function<ModOptions, Double> func;
    private final BiConsumer<ModOptions, Double> biConsumer;
    private final BiFunction<ModOptions, DoubleModOption, String> biFunction;

    DoubleModOption(String optionKey, double low, double high, float v, Function<ModOptions, Double> func1, BiConsumer<ModOptions, Double> bi1, BiFunction<ModOptions, DoubleModOption, String> func2) {
        super(optionKey);
        this.lowLimit = low;
        this.highLimit = high;
        this.value = v;
        this.func = func1;
        this.biConsumer = bi1;
        this.biFunction = func2;
    }

    @Override
    public AbstractButtonWidget createOptionButton(ModOptions options, int x, int y, int width) {
        return new ModOptionSliderWidget(options, x, y, width,20, this);
    }

    public double toPercent(double double_1) {
        return MathHelper.clamp((this.roundValue(double_1) - this.lowLimit) / (this.highLimit - this.lowLimit), 0.0D, 1.0D);
    }

    public double fromPercent(double double_1) {
        return this.roundValue(MathHelper.lerp(MathHelper.clamp(double_1, 0.0D, 1.0D), this.lowLimit, this.highLimit));
    }

    private double roundValue(double double_1) {
        if (this.value > 0.0F) {
            double_1 = (double)(this.value * (float)Math.round(double_1 / (double)this.value));
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

    public void setValue(ModOptions gameOptions_1, double double_1) {
        this.biConsumer.accept(gameOptions_1, double_1);
    }

    public double getValue(ModOptions gameOptions_1) {
        return this.func.apply(gameOptions_1).doubleValue();
    }

    public String getValueString(ModOptions gameOptions_1) {
        return this.biFunction.apply(gameOptions_1, this);
    }

}
