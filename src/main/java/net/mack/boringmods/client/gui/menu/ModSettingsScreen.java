package net.mack.boringmods.client.gui.menu;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.mack.boringmods.client.options.ModOption;
import net.mack.boringmods.client.options.ModOptions;
import net.minecraft.client.gui.Screen;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.TranslatableTextComponent;

@Environment(EnvType.CLIENT)
public class ModSettingsScreen extends Screen {
    private final static ModOption[] SETTING = new ModOption[]{
            ModOption.EXCAVATE_MAX_BLOCKS, ModOption.LIGHT_OVERLAY_ENABLE
    };

    private final Screen parent;
    private final ModOptions options;

    public ModSettingsScreen(Screen screen, ModOptions modOptions) {
        super(new TranslatableTextComponent("boringmods.options.title", new Object[0]));
        this.parent = screen;
        this.options = modOptions;
    }

    protected void onInitialized() {
        int index = 0;
        for (ModOption option : SETTING) {
            int x = this.screenWidth / 2 - 155 + index * 2 * 160;
            int y = this.screenHeight / 6 + 24 * (index >> 1);
            AbstractButtonWidget button = this.addButton(
                    option.createOptionButton(options, x, y, 150)
            );
            ++index;
        }

        this.addButton(new ButtonWidget(this.screenWidth / 2 - 100, this.screenHeight - 27, 200, 20, I18n.translate("gui.done", new Object[0]), (buttonWidget_1) -> {
            this.client.options.write();
//            this.client.window.method_4475();
            this.client.openScreen(this.parent);
        }));
    }

    public void render(int int_1, int int_2, float float_1) {
        this.drawBackground();
        super.render(int_1, int_2, float_1);
    }
}
