package net.mack.boringmods.client.gui.menu;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.mack.boringmods.client.options.ModConfigs;
import net.mack.boringmods.client.options.Config;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Screen;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.Window;
import net.minecraft.text.TranslatableTextComponent;

@Environment(EnvType.CLIENT)
public class ModConfigsScreen extends Screen implements Runnable {
    private final static Config[] SETTING = new Config[]{
            Config.TUNNEL_WIDTH, Config.LIGHT_OVERLAY_ENABLE,
            Config.TUNNEL_HEIGHT, Config.LIGHT_OVERLAY_RANGE,
            Config.TUNNEL_LONG, Config.EXCAVATE_RANGE,
            Config.PICKUP_DISTANCE, Config.EXCAVATE_MAX_BLOCKS
    };

    private final Screen parent;
    private final ModConfigs options;

    public ModConfigsScreen(Screen screen, ModConfigs modOptions) {
        super(new TranslatableTextComponent("boringmods.configs.title", new Object[0]));
        this.parent = screen;
        this.options = modOptions;
    }

    @Override
    protected void init() {
//        Window window = MinecraftClient.getInstance().window;
//        ModConfigs.LOGGER.info("ModConfigsScreen width {}, height {}", this.width, this.height);
//        ModConfigs.LOGGER.info("Minecraft window width {}, frame buffer width {}, scaled width {}, scale factor {}",
//                window.getWidth(), window.getFramebufferWidth(), window.getScaledWidth(), window.getScaleFactor());
        int index = 0;
        int x1 = this.width / 2 - 155;
        int x2 = x1 + 160;
        int y = this.height / 6 - 24;
        for (Config option : SETTING) {
            int x = x2;
            if (index % 2 == 0) {
                x = x1;
                y += 24;
            }
            if (null != option) {
                AbstractButtonWidget button = this.addButton(
                        option.createOptionButton(options, x, y, 150)
                );
            }
            ++index;
        }

        this.addButton(new ButtonWidget(this.width / 2 - 100, this.height - 27, 200, 20, I18n.translate("gui.done", new Object[0]), (buttonWidget_1) -> {
            this.options.write();
//            this.client.window.method_4475();

            if (null == this.minecraft)
                return;
            this.minecraft.openScreen(this.parent);
        }));
    }

    @Override
    public void render(int int_1, int int_2, float float_1) {
        this.renderBackground();
        super.render(int_1, int_2, float_1);
    }


    /**
     * When an object implementing interface <code>Runnable</code> is used
     * to create a thread, starting the thread causes the object's
     * <code>run</code> method to be called in that separately executing
     * thread.
     * <p>
     * The general contract of the method <code>run</code> is that it may
     * take any action whatsoever.
     *
     * @see Thread#run()
     */
    @Override
    public void run() {
        ModConfigsScreen screen = new ModConfigsScreen(
                MinecraftClient.getInstance().currentScreen,
                ModConfigs.INSTANCE);
        MinecraftClient.getInstance().openScreen(screen);
    }
}
