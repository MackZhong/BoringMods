package net.mack.boringmods.client.options;

import com.google.gson.Gson;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.resource.language.I18n;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

public class ModConfigs {
    public static final String MOD_ID = "boringmods";
    public static final Logger LOGGER = LogManager.getLogger();
    private static final Path configFile = new File(FabricLoader.getInstance().getConfigDirectory(), "boringmods.json").toPath();
    private static final Gson GSON = new Gson();
    public static final ModConfigs INSTANCE = initInstance();
    public static boolean menuAdded = false;

    private static ModConfigs initInstance() {
        ModConfigs instance = null;
        File file = configFile.toFile();
        if (!file.exists()) {
            try {
                instance = new ModConfigs();
                if (!file.createNewFile()) {
                    ModConfigs.LOGGER.error("[BoringMods Error]Create config file failed.");
                } else {
                    instance.write();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            try {
                String input = new String(Files.readAllBytes(configFile));
                instance = GSON.fromJson(input, ModConfigs.class);
            } catch (Exception e) {
                LOGGER.error("[BoringMods Error]Config file invalid.");
                e.printStackTrace();
            }
        }

        if (null == instance || !instance.key.equals("boringmods_1.0")) {
            ModConfigs.LOGGER.error("[BoringMods Error]Mod configs error.");
        }

        return instance;
    }

    public void write() {
        try {
            Files.write(configFile, GSON.toJson(this).getBytes());
        } catch (Exception ex) {
            LOGGER.error("[BoringMods Error]Failed to save mod options.", ex);
        }
    }

    private String key = "boringmods_1.0";
    public int excavateMaxBlocks = 64;
    public int excavateRange = 8;
    public int tunnelLong = 16;
    public int tunnelWidth = 3;
    public int tunnelHeight = 3;
    public boolean lightOverlayEnabled = true;
    public int lightOverlayRange = 16;
    public double pickupDistance = 16.0F;

    public final static DoubleConfig TUNNEL_WIDTH;
    public final static DoubleConfig TUNNEL_HEIGHT;
    public final static DoubleConfig TUNNEL_LONG;
    public final static DoubleConfig EXCAVATE_MAX_BLOCKS;
    public final static DoubleConfig EXCAVATE_RANGE;
    public final static BooleanConfig LIGHT_OVERLAY_ENABLE;
    public final static DoubleConfig LIGHT_OVERLAY_RANGE;
    public final static DoubleConfig PICKUP_DISTANCE;

    static {
        TUNNEL_WIDTH = new DoubleConfig(
                "configs.boringmods.tunnel_width",
                1.0D,
                8.0D,
                1.0F,
                (modOptions) -> (double) modOptions.tunnelWidth,
                (modOptions, aDouble) -> modOptions.tunnelWidth = aDouble.intValue(),
                (modOptions, doubleModOption) -> {
                    double d = doubleModOption.getValue(modOptions);
                    return doubleModOption.getKeyName() + I18n.translate("configs.boringmods.blocks", (int) d);
                });
        TUNNEL_HEIGHT = new DoubleConfig(
                "configs.boringmods.tunnel_height",
                2.0D,
                8.0D,
                1.0F,
                (modOptions) -> (double) modOptions.tunnelHeight,
                (modOptions, aDouble) -> modOptions.tunnelHeight = aDouble.intValue(),
                (modOptions, doubleModOption) -> {
                    double d = doubleModOption.getValue(modOptions);
                    return doubleModOption.getKeyName() + I18n.translate("configs.boringmods.blocks", (int) d);
                });
        TUNNEL_LONG = new DoubleConfig(
                "configs.boringmods.tunnel_long",
                2.0D,
                32.0D,
                1.0F,
                (modOptions) -> (double) modOptions.tunnelLong,
                (modOptions, aDouble) -> modOptions.tunnelLong = aDouble.intValue(),
                (modOptions, doubleModOption) -> {
                    double d = doubleModOption.getValue(modOptions);
                    return doubleModOption.getKeyName() + I18n.translate("configs.boringmods.blocks", (int) d);
                });
        LIGHT_OVERLAY_ENABLE = new BooleanConfig(
                "configs.boringmods.light_overlay",
                (modOptions) -> modOptions.lightOverlayEnabled,
                (modOptions, enabled) -> modOptions.lightOverlayEnabled = enabled);
        LIGHT_OVERLAY_RANGE = new DoubleConfig(
                "configs.boringmods.light_overlay_range",
                8.0D,
                32.0D,
                8.0F,
                (modOptions) -> (double) modOptions.lightOverlayRange,
                (modOptions, aDouble) -> modOptions.lightOverlayRange = aDouble.intValue(),
                (modOptions, doubleModOption) -> {
                    double d = doubleModOption.getValue(modOptions);
                    return doubleModOption.getKeyName() + I18n.translate("configs.boringmods.blocks", (int) d);
                });
        EXCAVATE_RANGE = new DoubleConfig(
                "configs.boringmods.excavate_range",
                2.0D,
                16.0D,
                1.0F,
                (options) -> (double) options.excavateRange,
                (options, aDouble) -> options.excavateRange = aDouble.intValue(),
                (options, doubleModOption) -> {
                    double d = doubleModOption.getValue(options);
                    return doubleModOption.getKeyName() + I18n.translate("configs.boringmods.blocks", (int) d);
                });
        EXCAVATE_MAX_BLOCKS = new DoubleConfig(
                "configs.boringmods.max_blocks",
                8.0D,
                128.0D,
                1.0F,
                (options) -> (double) options.excavateMaxBlocks,
                (options, aDouble) -> options.excavateMaxBlocks = aDouble.intValue(),
                (options, doubleModOption) -> {
                    double d = doubleModOption.getValue(options);
                    return doubleModOption.getKeyName() + I18n.translate("configs.boringmods.blocks", (int) d);
                });
        PICKUP_DISTANCE = new DoubleConfig(
                "configs.boringmods.pickup_distance",
                2.0D,
                32.0D,
                1.0F,
                (options) -> (double) options.pickupDistance,
                (options, aDouble) -> options.pickupDistance = aDouble.intValue(),
                (options, doubleModOption) -> {
                    double d = doubleModOption.getValue(options);
                    return doubleModOption.getKeyName() + I18n.translate("configs.boringmods.blocks", (int) d);
                });
    }
}
