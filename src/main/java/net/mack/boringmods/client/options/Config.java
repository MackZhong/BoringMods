package net.mack.boringmods.client.options;

import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.resource.language.I18n;

public abstract class Config {
    private final String key;

    Config(String optionKey) {
        this.key = optionKey;
    }

    public abstract AbstractButtonWidget createOptionButton(ModConfigs options, int x, int y, int width);

    String getKeyName() {
        return I18n.translate(this.key, new Object[0]) + ": ";
    }

}
