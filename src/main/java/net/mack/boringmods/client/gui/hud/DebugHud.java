//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package net.mack.boringmods.client.gui.hud;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.datafixers.DataFixUtils;

import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
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
import net.minecraft.fluid.*;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.IntegerProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.tag.TagManager;
import net.minecraft.text.TextFormat;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.LightType;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkPos;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.chunk.light.LightingProvider;

@Environment(EnvType.CLIENT)
public class DebugHud extends Drawable {
    private final MinecraftClient client;
    private final FontRenderer fontRenderer;
    private Entity player;
    private HitResult blockHit;
    private HitResult fluidHit;
    @Nullable
    private ChunkPos chunkPos;
    @Nullable
    private WorldChunk chunk;
    @Nullable
    private CompletableFuture<WorldChunk> chunkFuture;

    private org.apache.logging.log4j.Logger logger = org.apache.logging.log4j.LogManager.getLogger("boringmods");

    public DebugHud(MinecraftClient minecraftClient_1) {
        this.client = minecraftClient_1;
        this.fontRenderer = minecraftClient_1.fontRenderer;
        logger.info("QuickInfo hud initialized.");
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

        this.client.getProfiler().pop();
    }

    private void drawLeftText() {
        List<String> infos = this.getLeftText();
//        infos.add("");
//        infos.add("Debug: Pie [shift]: " + (this.client.options.debugProfilerEnabled ? "visible" : "hidden") + " FPS [alt]: " + (this.client.options.debugTpsEnabled ? "visible" : "hidden"));
//        infos.add("For help: press F3 + Q");

        int lineHeight = this.fontRenderer.fontHeight + 1;
        int top = 2;
        for (String ling : infos) {
            if (!Strings.isNullOrEmpty(ling)) {
//                this.fontRenderer.getClass();
                int stringWidth = this.fontRenderer.getStringWidth(ling);
//                int int_4 = true;
                drawRect(1, top - 1, 2 + stringWidth + 1, top + lineHeight - 1, -1873784752);
                this.fontRenderer.draw(ling, 2.0F, (float) top, 14737632);
                top += lineHeight;
            }
        }
    }

    private void drawRightText() {
        List<String> infos = this.getRightText();

        int lineHeight = this.fontRenderer.fontHeight + 1;
        int top = 2;
        int scaledWidth = this.client.window.getScaledWidth();
        for (String line : infos) {
            if (!Strings.isNullOrEmpty(line)) {
//                this.fontRenderer.getClass();
                int stringWidth = this.fontRenderer.getStringWidth(line);
                int left = scaledWidth - 2 - stringWidth;
                drawRect(left - 1, top - 1, left + stringWidth + 1, top + lineHeight - 1, -1873784752);
                this.fontRenderer.draw(line, (float) left, (float) top, 14737632);
                top += lineHeight;
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
        }

        BlockPos blockPos_1 = new BlockPos(this.player.x, this.player.getBoundingBox().minY, this.player.z);

        ChunkPos chunkPos_1 = new ChunkPos(blockPos_1);
        if (!Objects.equals(this.chunkPos, chunkPos_1)) {
            this.chunkPos = chunkPos_1;
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

    private World getWorld() {
        return DataFixUtils.orElse(Optional.ofNullable(this.client.getServer()).map((integratedServer_1) -> integratedServer_1.getWorld(this.client.world.dimension.getType())), this.client.world);
    }

    @Nullable
    private WorldChunk getChunk() {
        if (this.chunkFuture == null) {
            IntegratedServer integratedServer_1 = this.client.getServer();
            if (integratedServer_1 != null) {
                ServerWorld serverWorld_1 = integratedServer_1.getWorld(this.client.world.dimension.getType());
                if (serverWorld_1 != null && this.chunkPos != null) {
                    this.chunkFuture = serverWorld_1.method_16177(this.chunkPos.x, this.chunkPos.z, false);
                }
            }

            if (this.chunkFuture == null) {
                this.chunkFuture = CompletableFuture.completedFuture(this.getClientChunk());
            }
        }

        return this.chunkFuture.getNow(null);
    }

    private WorldChunk getClientChunk() {
        if (this.chunk == null && this.chunkPos != null) {
            this.chunk = this.client.world.getWorldChunk(this.chunkPos.x, this.chunkPos.z);
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
                infos.add(TextFormat.UNDERLINE + String.valueOf(Registry.ENTITY_TYPE.getId(target.getType())));
                infos.add(target.getDisplayName().getFormattedText());
            } else if (this.blockHit.getType() == HitResult.Type.BLOCK) {
                pos = ((BlockHitResult) this.blockHit).getBlockPos();
                BlockState blockState = this.client.world.getBlockState(pos);
                Block block = blockState.getBlock();
                infos.add(TextFormat.UNDERLINE + String.valueOf(Registry.BLOCK.getId(block)));
                infos.add(blockState.getBlock().getTextComponent().getFormattedText());

                // properties
                for (Property<?> property : blockState.getProperties()) {
                    if (property.getName().equals("age")) {
                        IntegerProperty key = (IntegerProperty) property;
                        Object[] values = key.getValues().toArray();
                        Integer maxAge = 0;
                        for (Object v : values) {
                            if ((Integer) v > maxAge) {
                                maxAge = (Integer) v;
                            }
                        }
                        Integer age = blockState.get(key);
                        TextFormat format;
                        if (maxAge.equals(age)) {
                            format = TextFormat.GOLD;
                        } else {
                            format = TextFormat.GREEN;
                        }
                        infos.add(String.format(Locale.getDefault(), "%s%d/%d", format, age, maxAge));
                    } else {
                        infos.add(String.format("%s=%s", property.getName(), blockState.get(property)));
                        infos.add(property.getValues().toString());
                    }
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
                FluidState fluidState = this.client.world.getFluidState(pos);
                if (!fluidState.isEmpty()) {
                    Fluid fluid = fluidState.getFluid();
                    BlockState blockState = fluidState.getBlockState();
                    infos.add(TextFormat.UNDERLINE + String.valueOf(Registry.FLUID.getId(fluid)));
                    infos.add(((fluid instanceof LavaFluid) ? TextFormat.RED : TextFormat.RESET) +
                            blockState.getBlock().getTextComponent().getFormattedText());

                    // properties
                    Boolean falling = fluidState.get(BaseFluid.FALLING);
                    infos.add(String.format("Falling: %b, Level: %d", falling, fluid.getLevel(fluidState)));
                    for (Property<?> property : blockState.getProperties()) {
                        if (Properties.FALLING == property || Properties.FLUID_BLOCK_LEVEL == property || Properties.FLUID_LEVEL == property) {
                            continue;
                        }
                        infos.add(String.format("%s=%s", property.getName(), blockState.get(property)));
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
    }
}
