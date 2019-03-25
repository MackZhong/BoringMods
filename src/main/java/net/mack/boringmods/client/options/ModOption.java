package net.mack.boringmods.client.options;

import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.resource.language.I18n;

public abstract class ModOption {
    private final String key;

    ModOption(String optionKey) {
        this.key = optionKey;
    }

    public abstract AbstractButtonWidget createOptionButton(ModOptions options, int x, int y, int width);

    public String getKeyName() {
        return I18n.translate(this.key, new Object[0]) + ": ";
    }

    public static final DoubleModOption EXCAVATE_MAX_BLOCKS;
    public static final BooleanModOption LIGHT_OVERLAY_ENABLE;

    static {
        EXCAVATE_MAX_BLOCKS = new DoubleModOption(
                "configs.boringmods.maxblocks",
                0.0D,
                1.0D,
                1.0F,
                (options) -> {
                    return (double) options.excavateMaxBlocks;
                },
                (options, aDouble) -> {
                    options.excavateMaxBlocks = aDouble.intValue();
                },
                (options, doubleModOption) -> {
                    double d = doubleModOption.getValue(options);
                    return doubleModOption.getKeyName() + I18n.translate("configs.boringmods.blocks", new Object[]{d});
                });
        LIGHT_OVERLAY_ENABLE = new BooleanModOption(
                "configs.boringmods.lightoverlay",
                (modOptions) -> {
                    return modOptions.lightOverlayEnabled;
                },
                (modOptions, enabled) -> {
                    modOptions.lightOverlayEnabled = enabled;
                });

    }

}
