package net.mack.boringmods.client.options;

import net.mack.boringmods.client.gui.button.OptionButtonWidget;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.resource.language.I18n;

import java.util.function.BiConsumer;
import java.util.function.Predicate;

public class BooleanConfig extends Config {
    private final Predicate<ModConfigs> predicate;
    private final BiConsumer<ModConfigs, Boolean> biConsumer;

    public BooleanConfig(String key, Predicate<ModConfigs> predicateOptions, BiConsumer<ModConfigs, Boolean> biConsumerOptions) {
        super(key);
        this.predicate = predicateOptions;
        this.biConsumer = biConsumerOptions;
    }

    public void set(ModConfigs options, String value){
        this.setValue(options, "true".equalsIgnoreCase(value));
    }

    public void toggle(ModConfigs options) {
        this.setValue(options, !this.getValue(options));
        options.write();
    }

    public void setValue(ModConfigs options, boolean value){
        this.biConsumer.accept(options, value);
    }

    public boolean getValue(ModConfigs options) {
        return this.predicate.test(options);
    }

    @Override
    public AbstractButtonWidget createOptionButton(ModConfigs options, int x, int y, int width) {
        return new OptionButtonWidget(x, y, width, 20, this, this.getValueString(options),
                (buttonWidget -> {
                    this.toggle(options);
                    buttonWidget.setMessage(this.getValueString(options));
                }));
    }

    private String getValueString(ModConfigs options) {
        return this.getKeyName() + I18n.translate(this.getValue(options) ? "options.on" : "options.off", new Object[0]);
    }
}
