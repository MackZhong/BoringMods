//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package net.mack.boringmods.client.gui.hud;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.UnmodifiableIterator;
import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.datafixers.DataFixUtils;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.longs.LongSets;

import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import javax.annotation.Nullable;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.SharedConstants;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.ClientBrandRetriever;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.FontRenderer;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.LavaFluid;
import net.minecraft.network.ClientConnection;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.Property;
import net.minecraft.tag.TagManager;
import net.minecraft.text.TextFormat;
import net.minecraft.util.Identifier;
import net.minecraft.util.MetricsData;
import net.minecraft.util.SystemUtil;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.LightType;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkPos;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.chunk.light.LightingProvider;
import net.minecraft.world.dimension.DimensionType;

@Environment(EnvType.CLIENT)
public class DebugHud extends Drawable {
    private final MinecraftClient client;
    private final FontRenderer fontRenderer;
    private Entity player;
    private HitResult blockHit;
    private HitResult fluidHit;
    @Nullable
    private ChunkPos pos;
    @Nullable
    private WorldChunk chunk;
    @Nullable
    private CompletableFuture<WorldChunk> chunkFuture;

    public DebugHud(MinecraftClient minecraftClient_1) {
        this.client = minecraftClient_1;
        this.fontRenderer = minecraftClient_1.fontRenderer;
    }

    private void resetChunk() {
        this.chunkFuture = null;
        this.chunk = null;
    }

    public void draw() {
        this.client.getProfiler().push("debug");
        GlStateManager.pushMatrix();
        this.player = this.client.getCameraEntity();
        if (null == this.player) {
            this.player = this.client.player;
        }
        this.blockHit = this.client.hitResult;// this.player.rayTrace(20.0D, 0.0F, false);
        this.fluidHit = this.player.rayTrace(20.0D, 0.0F, true);
        this.drawLeftText();
        this.drawRightText();
        GlStateManager.popMatrix();
        if (this.client.options.debugTpsEnabled) {
            int int_1 = this.client.window.getScaledWidth();
            this.drawMetricsData(this.client.getMetricsData(), 0, int_1 / 2, true);
            IntegratedServer integratedServer_1 = this.client.getServer();
            if (integratedServer_1 != null) {
                this.drawMetricsData(integratedServer_1.getMetricsData(), int_1 - Math.min(int_1 / 2, 240), int_1 / 2, false);
            }
        }

        this.client.getProfiler().pop();
    }

    private void drawLeftText() {
        List<String> list_1 = this.getLeftText();
//        list_1.add("");
//        list_1.add("Debug: Pie [shift]: " + (this.client.options.debugProfilerEnabled ? "visible" : "hidden") + " FPS [alt]: " + (this.client.options.debugTpsEnabled ? "visible" : "hidden"));
//        list_1.add("For help: press F3 + Q");

        for (int int_1 = 0; int_1 < list_1.size(); ++int_1) {
            String string_1 = (String) list_1.get(int_1);
            if (!Strings.isNullOrEmpty(string_1)) {
//                this.fontRenderer.getClass();
                int int_2 = 9;
                int int_3 = this.fontRenderer.getStringWidth(string_1);
//                int int_4 = true;
                int int_5 = 2 + int_2 * int_1;
                drawRect(1, int_5 - 1, 2 + int_3 + 1, int_5 + int_2 - 1, -1873784752);
                this.fontRenderer.draw(string_1, 2.0F, (float) int_5, 14737632);
            }
        }

    }

    private void drawRightText() {
        List<String> list_1 = this.getRightText();

        for (int int_1 = 0; int_1 < list_1.size(); ++int_1) {
            String string_1 = (String) list_1.get(int_1);
            if (!Strings.isNullOrEmpty(string_1)) {
                this.fontRenderer.getClass();
                int int_2 = 9;
                int int_3 = this.fontRenderer.getStringWidth(string_1);
                int int_4 = this.client.window.getScaledWidth() - 2 - int_3;
                int int_5 = 2 + int_2 * int_1;
                drawRect(int_4 - 1, int_5 - 1, int_4 + int_3 + 1, int_5 + int_2 - 1, -1873784752);
                this.fontRenderer.draw(string_1, (float) int_4, (float) int_5, 14737632);
            }
        }

    }

    private List<String> getLeftText() {
        if (this.client.hasReducedDebugInfo()) {
            return Lists.newArrayList("Minecraft " + SharedConstants.getGameVersion().getName() + " (" + this.client.getGameVersion() + "/" + ClientBrandRetriever.getClientModName() + ")",
                    this.client.fpsDebugString,
                    this.client.worldRenderer.getChunksDebugString(),
                    this.client.worldRenderer.getEntitiesDebugString(),
                    "P: " + this.client.particleManager.getDebugString() + ". T: " + this.client.world.getEntityCountAsString(),
                    this.client.world.getChunkProviderStatus());
        } else {
            BlockPos blockPos_1 = new BlockPos(this.player.x, this.player.getBoundingBox().minY, this.player.z);

            ChunkPos chunkPos_1 = new ChunkPos(blockPos_1);
            if (!Objects.equals(this.pos, chunkPos_1)) {
                this.pos = chunkPos_1;
                this.resetChunk();
            }

            World world_1 = this.getWorld();
            Direction facing = this.player.getHorizontalFacing();
            List<String> list_1 = Lists.newArrayList(I18n.translate("quickinfo." + facing.asString()),
                    String.format(Locale.ROOT, "XYZ: %.3f / %.5f / %.3f", this.player.x, this.player.getBoundingBox().minY, this.player.z),
                    String.format("Block: %d %d %d", blockPos_1.getX(), blockPos_1.getY(), blockPos_1.getZ()));
            if (this.client.world != null) {
                if (this.client.world.isBlockLoaded(blockPos_1)) {
                    WorldChunk worldChunk_1 = this.getClientChunk();
                    if (worldChunk_1.isEmpty()) {
                        list_1.add("Waiting for chunk...");
                    } else {
                        list_1.add("Client Light: " + worldChunk_1.getLightLevel(blockPos_1, 0) + " (" + this.client.world.getLightLevel(LightType.SKY_LIGHT, blockPos_1) + " sky, " + this.client.world.getLightLevel(LightType.BLOCK_LIGHT, blockPos_1) + " block)");
                        WorldChunk worldChunk_2 = this.getChunk();
                        if (worldChunk_2 != null) {
                            LightingProvider lightingProvider_1 = world_1.getChunkManager().getLightingProvider();
                            list_1.add("Server Light: (" + lightingProvider_1.get(LightType.SKY_LIGHT).getLightLevel(blockPos_1) + " sky, " + lightingProvider_1.get(LightType.BLOCK_LIGHT).getLightLevel(blockPos_1) + " block)");
                        }
                    }
                } else {
                    list_1.add("Outside of world...");
                }
            } else {
                list_1.add("Outside of world...");
            }

            return list_1;
        }
    }

    private World getWorld() {
        return DataFixUtils.orElse(Optional.ofNullable(this.client.getServer()).map((integratedServer_1) -> {
            return integratedServer_1.getWorld(this.client.world.dimension.getType());
        }), this.client.world);
    }

    @Nullable
    private WorldChunk getChunk() {
        if (this.chunkFuture == null) {
            IntegratedServer integratedServer_1 = this.client.getServer();
            if (integratedServer_1 != null) {
                ServerWorld serverWorld_1 = integratedServer_1.getWorld(this.client.world.dimension.getType());
                if (serverWorld_1 != null && this.pos != null) {
                    this.chunkFuture = serverWorld_1.method_16177(this.pos.x, this.pos.z, false);
                }
            }

            if (this.chunkFuture == null) {
                this.chunkFuture = CompletableFuture.completedFuture(this.getClientChunk());
            }
        }

        return this.chunkFuture.getNow(null);
    }

    private WorldChunk getClientChunk() {
        if (this.chunk == null && this.pos != null) {
            this.chunk = this.client.world.getWorldChunk(this.pos.x, this.pos.z);
        }

        return this.chunk;
    }

    private List<String> getRightText() {
        ClientPlayNetworkHandler net = this.client.getNetworkHandler();
        TagManager tagManager = null;
        if (null != net) {
            tagManager = net.getTagManager();
        }

        List<String> infos = Lists.newArrayList(getTimeDesc());

        if (this.client.hasReducedDebugInfo()) {
            return infos;
        } else {
            BlockPos pos;
            Entity target = this.client.targetedEntity;
            if (target != null) {
                infos.add("");
                infos.add(TextFormat.UNDERLINE + String.valueOf(Registry.ENTITY_TYPE.getId(target.getType())));
                infos.add(target.getDisplayName().getFormattedText());
            } else if (this.blockHit.getType() == HitResult.Type.BLOCK) {
                pos = ((BlockHitResult) this.blockHit).getBlockPos();
                BlockState state = this.client.world.getBlockState(pos);
                Block block = state.getBlock();
                infos.add(TextFormat.UNDERLINE + String.valueOf(Registry.BLOCK.getId(block)));
                infos.add(state.getBlock().getTextComponent().getFormattedText());

                // properties
                ImmutableMap<Property<?>, Comparable<?>> entries = state.getEntries();
                for (Property<?> property : entries.keySet()) {
                    infos.add(String.format("%s=%s", property.getName(), entries.get(property)));
                    infos.add(property.getValues().toString());
                }

                // tags
                if (null != tagManager) {
                    for (Identifier id : tagManager.blocks().getTagsFor(block)) {
                        infos.add(String.format("#%s", id));
                    }
                }
            }

            if (this.fluidHit.getType() == HitResult.Type.BLOCK) {
                pos = ((BlockHitResult) this.fluidHit).getBlockPos();
                FluidState state = this.client.world.getFluidState(pos);
                if (!state.isEmpty()) {
                    Fluid fluid = state.getFluid();
                    infos.add("");
                    infos.add(TextFormat.UNDERLINE + String.valueOf(Registry.FLUID.getId(fluid)));
                    infos.add(((fluid instanceof LavaFluid) ? TextFormat.RED : TextFormat.RESET) +
                            state.getBlockState().getBlock().getTextComponent().getFormattedText());

                    // properties
                    ImmutableMap<Property<?>, Comparable<?>> entries = state.getEntries();
                    for (Property<?> property : entries.keySet()) {
                        infos.add(String.format("%s=%s", property.getName(), entries.get(property)));
                        infos.add(property.getValues().toString());
                    }

                    // tags
                    if (null != tagManager) {
                        for (Identifier id : tagManager.fluids().getTagsFor(fluid)) {
                            infos.add(String.format("#%s", id));
                        }
                    }
                }
            }

            return infos;
        }
    }

    private String getTimeDesc() {
        long totalTime = this.getWorld().getTimeOfDay();
        long realDays = (totalTime + 6000) / 24000;
        long timeOfDays = totalTime % 24000;
        long hours = ((timeOfDays + 6000) / 1000) % 24;
        long minutes = ((timeOfDays % 1000) * 60) / 1000;

        return I18n.translate("quickinfo.days", realDays, hours, minutes);
//        return String.format("第%d天，%02d:%02d", realDays, hours, minutes);
    }

    private String propertyToString(Entry<Property<?>, Comparable<?>> map$Entry_1) {
        Property<?> property_1 = (Property) map$Entry_1.getKey();
        Comparable<?> comparable_1 = (Comparable) map$Entry_1.getValue();
        String string_1 = SystemUtil.getValueAsString(property_1, comparable_1);
        if (Boolean.TRUE.equals(comparable_1)) {
            string_1 = TextFormat.GREEN + string_1;
        } else if (Boolean.FALSE.equals(comparable_1)) {
            string_1 = TextFormat.RED + string_1;
        }

        return property_1.getName() + ": " + string_1;
    }

    private void drawMetricsData(MetricsData metricsData_1, int int_1, int int_2, boolean boolean_1) {
        GlStateManager.disableDepthTest();
        int int_3 = metricsData_1.method_15249();
        int int_4 = metricsData_1.getCurrentIndex();
        long[] longs_1 = metricsData_1.getSamples();
        int int_6 = int_1;
        int int_7 = Math.max(0, longs_1.length - int_2);
        int int_8 = longs_1.length - int_7;
        int int_5 = metricsData_1.wrapIndex(int_3 + int_7);
        long long_1 = 0L;
        int int_9 = 2147483647;
        int int_10 = -2147483648;

        int int_13;
        int int_14;
        for (int_13 = 0; int_13 < int_8; ++int_13) {
            int_14 = (int) (longs_1[metricsData_1.wrapIndex(int_5 + int_13)] / 1000000L);
            int_9 = Math.min(int_9, int_14);
            int_10 = Math.max(int_10, int_14);
            long_1 += (long) int_14;
        }

        int_13 = this.client.window.getScaledHeight();
        drawRect(int_1, int_13 - 60, int_1 + int_8, int_13, -1873784752);

        while (int_5 != int_4) {
            int_14 = metricsData_1.method_15248(longs_1[int_5], boolean_1 ? 30 : 60, boolean_1 ? 60 : 20);
            int int_15 = boolean_1 ? 100 : 60;
            int int_16 = this.method_1833(MathHelper.clamp(int_14, 0, int_15), 0, int_15 / 2, int_15);
            this.drawVerticalLine(int_6, int_13, int_13 - int_14, int_16);
            ++int_6;
            int_5 = metricsData_1.wrapIndex(int_5 + 1);
        }

        if (boolean_1) {
            drawRect(int_1 + 1, int_13 - 30 + 1, int_1 + 14, int_13 - 30 + 10, -1873784752);
            this.fontRenderer.draw("60 FPS", (float) (int_1 + 2), (float) (int_13 - 30 + 2), 14737632);
            this.drawHorizontalLine(int_1, int_1 + int_8 - 1, int_13 - 30, -1);
            drawRect(int_1 + 1, int_13 - 60 + 1, int_1 + 14, int_13 - 60 + 10, -1873784752);
            this.fontRenderer.draw("30 FPS", (float) (int_1 + 2), (float) (int_13 - 60 + 2), 14737632);
            this.drawHorizontalLine(int_1, int_1 + int_8 - 1, int_13 - 60, -1);
        } else {
            drawRect(int_1 + 1, int_13 - 60 + 1, int_1 + 14, int_13 - 60 + 10, -1873784752);
            this.fontRenderer.draw("20 TPS", (float) (int_1 + 2), (float) (int_13 - 60 + 2), 14737632);
            this.drawHorizontalLine(int_1, int_1 + int_8 - 1, int_13 - 60, -1);
        }

        this.drawHorizontalLine(int_1, int_1 + int_8 - 1, int_13 - 1, -1);
        this.drawVerticalLine(int_1, int_13 - 60, int_13, -1);
        this.drawVerticalLine(int_1 + int_8 - 1, int_13 - 60, int_13, -1);
        if (boolean_1 && this.client.options.maxFps > 0 && this.client.options.maxFps <= 250) {
            this.drawHorizontalLine(int_1, int_1 + int_8 - 1, int_13 - 1 - (int) (1800.0D / (double) this.client.options.maxFps), -16711681);
        }

        String string_1 = int_9 + " ms min";
        String string_2 = long_1 / (long) longs_1.length + " ms avg";
        String string_3 = int_10 + " ms max";
        FontRenderer var10000 = this.fontRenderer;
        float var10002 = (float) (int_1 + 2);
        int var10003 = int_13 - 60;
        this.fontRenderer.getClass();
        var10000.drawWithShadow(string_1, var10002, (float) (var10003 - 9), 14737632);
        var10000 = this.fontRenderer;
        var10002 = (float) (int_1 + int_8 / 2 - this.fontRenderer.getStringWidth(string_2) / 2);
        var10003 = int_13 - 60;
        this.fontRenderer.getClass();
        var10000.drawWithShadow(string_2, var10002, (float) (var10003 - 9), 14737632);
        var10000 = this.fontRenderer;
        var10002 = (float) (int_1 + int_8 - this.fontRenderer.getStringWidth(string_3));
        var10003 = int_13 - 60;
        this.fontRenderer.getClass();
        var10000.drawWithShadow(string_3, var10002, (float) (var10003 - 9), 14737632);
        GlStateManager.enableDepthTest();
    }

    private int method_1833(int int_1, int int_2, int int_3, int int_4) {
        return int_1 < int_3 ? this.interpolateColor(-16711936, -256, (float) int_1 / (float) int_3) : this.interpolateColor(-256, -65536, (float) (int_1 - int_3) / (float) (int_4 - int_3));
    }

    private int interpolateColor(int int_1, int int_2, float float_1) {
        int int_3 = int_1 >> 24 & 255;
        int int_4 = int_1 >> 16 & 255;
        int int_5 = int_1 >> 8 & 255;
        int int_6 = int_1 & 255;
        int int_7 = int_2 >> 24 & 255;
        int int_8 = int_2 >> 16 & 255;
        int int_9 = int_2 >> 8 & 255;
        int int_10 = int_2 & 255;
        int int_11 = MathHelper.clamp((int) MathHelper.lerp(float_1, (float) int_3, (float) int_7), 0, 255);
        int int_12 = MathHelper.clamp((int) MathHelper.lerp(float_1, (float) int_4, (float) int_8), 0, 255);
        int int_13 = MathHelper.clamp((int) MathHelper.lerp(float_1, (float) int_5, (float) int_9), 0, 255);
        int int_14 = MathHelper.clamp((int) MathHelper.lerp(float_1, (float) int_6, (float) int_10), 0, 255);
        return int_11 << 24 | int_12 << 16 | int_13 << 8 | int_14;
    }

    private static long method_1838(long long_1) {
        return long_1 / 1024L / 1024L;
    }
}
