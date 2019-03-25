package net.mack.boringmods.client.options;

import net.mack.boringmods.client.gui.button.OptionButtonWidget;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.resource.language.I18n;

import java.util.function.BiConsumer;
import java.util.function.Predicate;

public class BooleanModOption extends ModOption {
    private final Predicate<ModOptions> predicate;
    private final BiConsumer<ModOptions, Boolean> biConsumer;

    public BooleanModOption(String key, Predicate<ModOptions> predicateOptions, BiConsumer<ModOptions, Boolean> biConsumerOptions) {
        super(key);
        this.predicate = predicateOptions;
        this.biConsumer = biConsumerOptions;
    }

    public void set(ModOptions options, String value){
        this.setValue(options, "true".equalsIgnoreCase(value));
    }

    private void toggle(ModOptions options) {
        this.setValue(options, !this.getValue(options));
        options.write();
    }

    public void setValue(ModOptions options, boolean value){
        this.biConsumer.accept(options, value);
    }

    private boolean getValue(ModOptions options) {
        return this.predicate.test(options);
    }

    @Override
    public AbstractButtonWidget createOptionButton(ModOptions options, int x, int y, int width) {
        return new OptionButtonWidget(x, y, width, 20, this, this.getValueString(options),
                (buttonWidget -> {
                    this.toggle(options);
                    buttonWidget.setMessage(this.getValueString(options));
                }));
    }

    private String getValueString(ModOptions options) {
        return this.getKeyName() + I18n.translate(this.getValue(options) ? "options.on" : "options.off", new Object[0]);
    }
}
