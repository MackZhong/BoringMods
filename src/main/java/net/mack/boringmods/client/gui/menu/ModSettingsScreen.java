package net.mack.boringmods.client.gui.menu;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.options.GameOptions;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TranslatableTextComponent;

@Environment(EnvType.CLIENT)
public class ModSettingsScreen extends Screen {
    private final Screen parent;
    private final GameOptions gameOptions;

    protected ModSettingsScreen(Screen screen, GameOptions options) {
        super(new TranslatableTextComponent("boringmods.options.title", new Object[0]));
        this.parent = screen;
        this.gameOptions = options;
    }

    protected void onInitialized() {
        this.addButton(new ButtonWidget(this.screenWidth / 2 - 100, this.screenHeight - 27, 200, 20, I18n.translate("gui.done", new Object[0]), (buttonWidget_1) -> {
            this.client.options.write();
//            this.client.window.method_4475();
            this.client.openScreen(this.parent);
        }));
    }
}
