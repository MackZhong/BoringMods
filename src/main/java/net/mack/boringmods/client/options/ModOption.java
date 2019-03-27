package net.mack.boringmods.client.options;

import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.resource.language.I18n;

public abstract class ModOption {
    private final String key;

    ModOption(String optionKey) {
        this.key = optionKey;
    }

    public abstract AbstractButtonWidget createOptionButton(ModOptions options, int x, int y, int width);

    String getKeyName() {
        return I18n.translate(this.key, new Object[0]) + ": ";
    }

    public static final DoubleModOption EXCAVATE_MAX_BLOCKS;
    public static final DoubleModOption EXCAVATE_RANGE;
    public static final BooleanModOption LIGHT_OVERLAY_ENABLE;
    public static final DoubleModOption LIGHT_OVERLAY_RANGE;
    public static final DoubleModOption TUNNEL_WIDTH;
    public static final DoubleModOption TUNNEL_HEIGHT;
    public static final DoubleModOption TUNNEL_LONG;

    static {
        EXCAVATE_MAX_BLOCKS = new DoubleModOption(
                "configs.boringmods.maxblocks",
                8.0D,
                256.0D,
                1.0F,
                (options) -> (double) options.excavateMaxBlocks,
                (options, aDouble) -> options.excavateMaxBlocks = aDouble.intValue(),
                (options, doubleModOption) -> {
                    double d = doubleModOption.getValue(options);
                    return doubleModOption.getKeyName() + I18n.translate("configs.boringmods.blocks", d);
                });
        EXCAVATE_RANGE = new DoubleModOption(
                "configs.boringmods.excavaterange",
                2.0D,
                16.0D,
                1.0F,
                (options) -> (double) options.excavateRange,
                (options, aDouble) -> options.excavateRange = aDouble.intValue(),
                (options, doubleModOption) -> {
                    double d = doubleModOption.getValue(options);
                    return doubleModOption.getKeyName() + I18n.translate("configs.boringmods.blocks", d);
                });
        LIGHT_OVERLAY_RANGE = new DoubleModOption(
                "configs.boringmods.lightoverlayrange",
                8.0D,
                64.0D,
                8.0F,
                (modOptions) -> (double) modOptions.lightOverlayRange,
                (modOptions, aDouble) -> modOptions.lightOverlayRange = aDouble.intValue(),
                (modOptions, doubleModOption) -> {
                    double d = doubleModOption.getValue(modOptions);
                    return doubleModOption.getKeyName() + I18n.translate("configs.boringmods.blocks", d);
                });
        LIGHT_OVERLAY_ENABLE = new BooleanModOption(
                "configs.boringmods.lightoverlay",
                (modOptions) -> modOptions.lightOverlayEnabled,
                (modOptions, enabled) -> modOptions.lightOverlayEnabled = enabled);
        TUNNEL_WIDTH = new DoubleModOption(
                "configs.boringmods.tunnelwidth",
                1.0D,
                8.0D,
                1.0F,
                (modOptions) -> (double) modOptions.tunnelWidth,
                (modOptions, aDouble) -> modOptions.tunnelWidth = aDouble.intValue(),
                (modOptions, doubleModOption) -> {
                    double d = doubleModOption.getValue(modOptions);
                    return doubleModOption.getKeyName() + I18n.translate("configs.boringmods.blocks", d);
                });
        TUNNEL_HEIGHT = new DoubleModOption(
                "configs.boringmods.tunnelheight",
                1.0D,
                8.0D,
                1.0F,
                (modOptions) -> (double) modOptions.tunnelHeight,
                (modOptions, aDouble) -> modOptions.tunnelHeight = aDouble.intValue(),
                (modOptions, doubleModOption) -> {
                    double d = doubleModOption.getValue(modOptions);
                    return doubleModOption.getKeyName() + I18n.translate("configs.boringmods.blocks", d);
                });
        TUNNEL_LONG = new DoubleModOption(
                "configs.boringmods.tunnellong",
                1.0D,
                8.0D,
                1.0F,
                (modOptions) -> (double) modOptions.tunnelLong,
                (modOptions, aDouble) -> modOptions.tunnelLong = aDouble.intValue(),
                (modOptions, doubleModOption) -> {
                    double d = doubleModOption.getValue(modOptions);
                    return doubleModOption.getKeyName() + I18n.translate("configs.boringmods.blocks", d);
                });
    }

}
