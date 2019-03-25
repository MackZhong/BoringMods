package net.mack.boringmods.client.options;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.util.math.MathHelper;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

@Environment(EnvType.CLIENT)
public class DoubleModOption extends ModOption {
    private final float field_18204;
    private final double field_18205;
    private double field_18206;
    private final Function<ModOptions, Double> func;
    private final BiConsumer<ModOptions, Double> biConsumer;
    private final BiFunction<ModOptions, DoubleModOption, String> biFunction;

    DoubleModOption(String optionKey, double low, double high, float v, Function<ModOptions, Double> func1, BiConsumer<ModOptions, Double> bi1, BiFunction<ModOptions, DoubleModOption, String> func2) {
        super(optionKey);
        this.field_18205 = low;
        this.field_18206 = high;
        this.field_18204 = v;
        this.func = func1;
        this.biConsumer = bi1;
        this.biFunction = func2;
    }

    @Override
    public AbstractButtonWidget createOptionButton(ModOptions options, int x, int y, int width) {
        return new ModOptionSliderWidget(options, x, y, width,20, this);
    }

    public double method_18611(double double_1) {
        return MathHelper.clamp((this.method_18618(double_1) - this.field_18205) / (this.field_18206 - this.field_18205), 0.0D, 1.0D);
    }

    public double method_18616(double double_1) {
        return this.method_18618(MathHelper.lerp(MathHelper.clamp(double_1, 0.0D, 1.0D), this.field_18205, this.field_18206));
    }

    private double method_18618(double double_1) {
        if (this.field_18204 > 0.0F) {
            double_1 = (double)(this.field_18204 * (float)Math.round(double_1 / (double)this.field_18204));
        }

        return MathHelper.clamp(double_1, this.field_18205, this.field_18206);
    }

    public double method_18615() {
        return this.field_18205;
    }

    public double method_18617() {
        return this.field_18206;
    }

    public void method_18612(float float_1) {
        this.field_18206 = (double)float_1;
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
