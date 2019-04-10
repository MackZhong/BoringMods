package net.mack.boringmods.client.gui.button;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.mack.boringmods.client.options.Config;
import net.minecraft.client.gui.widget.ButtonWidget;

@Environment(EnvType.CLIENT)
public class OptionButtonWidget extends ButtonWidget {
    private Config config;

    public OptionButtonWidget(int x, int y, int width, int height, Config option, String title, ButtonWidget.PressAction pressAction) {
        super(x, y, width, height, title, pressAction);
        this.config = option;
    }
}
