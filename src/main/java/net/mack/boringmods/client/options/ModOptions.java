package net.mack.boringmods.client.options;

import com.google.gson.Gson;
import net.fabricmc.loader.api.FabricLoader;
import net.mack.boringmods.client.gui.menu.ModSettingsScreen;
import net.minecraft.client.MinecraftClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class ModOptions implements Runnable {
    public static final String MOD_ID = "boringmods";
    public static final Logger LOGGER = LogManager.getLogger();
    public static final Gson GSON = new Gson();
    public static final ModOptions INSTANCE = new ModOptions();
    public boolean menuAdded = false;

    private File configFile;

    public int excavateMaxBlocks = 64;
    public int excavateRange = 8;
    public int tunnelLong = 19;
    public int tunnelWidth = 3;
    public int tunnelHeight = 3;
    public boolean lightOverlayEnabled = true;
    public int lightOverlayRange = 16;

    private ModOptions() {
        this.configFile = new File(FabricLoader.getInstance().getConfigDirectory(), "options.txt");

    }

    public void write() {
        try {
            PrintWriter writer = new PrintWriter(new OutputStreamWriter(new FileOutputStream(this.configFile), StandardCharsets.UTF_8));
            Throwable throwedError;

            try {

            } catch (Throwable throwed) {
                LOGGER.error("", throwed);
                throwedError = throwed;
                throw throwed;
            } finally {

            }
        } catch (Exception ex) {
            LOGGER.error("Failed to save mod options.", ex);
        }
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
        ModSettingsScreen screen = new ModSettingsScreen(
                MinecraftClient.getInstance().currentScreen,
                ModOptions.INSTANCE);
        MinecraftClient.getInstance().openScreen(screen);
    }
}
