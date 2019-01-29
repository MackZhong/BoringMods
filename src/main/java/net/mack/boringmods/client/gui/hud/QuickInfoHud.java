package net.mack.boringmods.client.gui.hud;

import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.datafixers.DataFixUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.FontRenderer;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.LavaFluid;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.server.world.ServerWorld;
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
import net.minecraft.world.chunk.ChunkNibbleArray;
import net.minecraft.world.chunk.ChunkPos;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.chunk.light.LightingProvider;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Environment(EnvType.CLIENT)
public class QuickInfoHud extends Drawable {
    private final MinecraftClient client;
    private final FontRenderer fontRenderer;
    private HitResult hitFluid;
    private Entity player;
    @Nullable
    private WorldChunk chunkClient;
    @Nullable
    private CompletableFuture<WorldChunk> chunkFuture;
    @Nullable
    private ChunkPos chunkPos;
    @Nullable
    private ChunkNibbleArray lightingArray;

    private org.apache.logging.log4j.Logger logger = org.apache.logging.log4j.LogManager.getLogger("BoringMods");

    public QuickInfoHud(MinecraftClient mcClient) {
        this.client = mcClient;
        this.fontRenderer = mcClient.fontRenderer;
        this.logger.info("QuickInfo Hud initialized.");
    }

    private void resetChunk() {
        this.chunkFuture = null;
        this.chunkClient = null;
        this.logger.info("Reset Chunk.");
    }

    public void draw(float esp) {
        this.client.getProfiler().push("quickinfo");

        this.player = this.client.getCameraEntity();
        if (null == this.player) {
            this.player = this.client.player;
        }

        GlStateManager.pushMatrix();
        this.hitFluid = this.player.rayTrace(20.0D, 0.0F, true);

        this.drawInfos();

//        this.drawLighting();
        GlStateManager.popMatrix();


        this.client.getProfiler().pop();
    }

    private void drawLighting() {
        if (null == this.lightingArray)
            return;
        for (Byte l : this.lightingArray.asByteArray()) {
            Byte b = l;
        }
    }

    private void drawInfos() {
        int maxLineWidth = 10;
        List<String> lines = getInfos();

        for (String line : lines) {
            maxLineWidth = Math.max(maxLineWidth, this.fontRenderer.getStringWidth(line));
        }
        maxLineWidth = (int) (Math.ceil(maxLineWidth / 5.0D + 0.5D) * 5);

        int top = 0;
        int scaleWidth = this.client.window.getScaledWidth();
        int lineHeight = this.fontRenderer.fontHeight + 2;
        int left = scaleWidth - maxLineWidth;
        drawRect(left, top, left + maxLineWidth, top + lines.size() * lineHeight + 2, 0x88B0B0B0);
        top++;
        left++;
        maxLineWidth--;
        for (String line : lines) {
            drawRect(left, top, left + maxLineWidth, top + lineHeight, 0xAA0000AA);
            this.fontRenderer.draw(line, left + 1, top + 1, 0x00E0E0E0);
            top += lineHeight;
        }
    }

    private List<String> getInfos() {
        List<String> infos = new ArrayList<>();
        if (this.client.hasReducedDebugInfo()){
            return infos;
        }

        BlockPos pos = this.player.getPos();
        pos = new BlockPos(pos.getX(), (int) this.player.getBoundingBox().minY, pos.getZ());
        Direction facing = this.player.getHorizontalFacing();
        infos.add(String.format("%d, %d, %d %s",
                pos.getX(), pos.getY(), pos.getZ(),
                I18n.translate("quickinfo." + facing.asString())));

        infos.add(getTimeDesc());

        World world = this.getWorld();

        ClientPlayNetworkHandler net = this.client.getNetworkHandler();
        TagManager tagManager = null;
        if (null != net) {
            tagManager = net.getTagManager();
        }

        // Entity
        Entity entity = this.client.targetedEntity;
        if (null != entity) {
            infos.add(TextFormat.UNDERLINE + String.valueOf(Registry.ENTITY_TYPE.getId(entity.getType())));
            TextFormat format = TextFormat.GRAY;
            if (entity instanceof Monster) {
                format = TextFormat.RED;
            } else if (entity instanceof PassiveEntity) {
                format = TextFormat.GREEN;
            }

            infos.add("Name: " + format + entity.getName().getFormattedText());
            infos.add("DisplayName: " + format + entity.getDisplayName().getFormattedText());
            if (null != entity.getCustomName())
                infos.add("CustomName: " + format + entity.getCustomName().getFormattedText());
        } else if (this.client.hitResult.getType() == HitResult.Type.BLOCK) {
            // Block
            pos = ((BlockHitResult) this.client.hitResult).getBlockPos();
            BlockState state = world.getBlockState(pos);
            if (!state.isAir()) {
                Block block = state.getBlock();
                infos.add(TextFormat.UNDERLINE + String.valueOf(Registry.BLOCK.getId(block)));
                infos.add(block.getTextComponent().getFormattedText() + "/" + block.getRenderLayer().name());

                // Lighting
                ChunkPos posChunk = new ChunkPos(pos);
                if (!Objects.equals(this.chunkPos, posChunk)) {
                    this.chunkPos = posChunk;
                    this.resetChunk();
                }
                if (world.isBlockLoaded(pos)) {
                    WorldChunk chunk = this.getClientChunk();
                    if (!chunk.isEmpty()) {
                        infos.add(I18n.translate("quickinfo.light.client",
                                chunk.getLightLevel(pos, 0), world.getLightLevel(LightType.SKY_LIGHT, pos), world.getLightLevel(LightType.BLOCK_LIGHT, pos)));
                        chunk = this.getChunk();
                        if (null != chunk) {
                            LightingProvider provider = world.getChunkManager().getLightingProvider();
                            infos.add(I18n.translate("quickinfo.light.server",
                                    provider.get(LightType.SKY_LIGHT).getLightLevel(pos), provider.get(LightType.BLOCK_LIGHT).getLightLevel(pos)));
                            this.lightingArray = provider.get(LightType.BLOCK_LIGHT).getChunkLightArray(pos.getX(), pos.getY(), pos.getZ());
                        }
                    }
                }

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
        }

        // Fluid
        if (null != this.hitFluid && this.hitFluid.getType() == HitResult.Type.BLOCK) {
            pos = ((BlockHitResult) this.hitFluid).getBlockPos();
            FluidState state = world.getFluidState(pos);
            if (!state.isEmpty()) {
                Fluid fluid = state.getFluid();
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

    private String getTimeDesc() {
        long totalTime = this.getWorld().getTimeOfDay();
        long realDays = (totalTime + 6000) / 24000;
        long timeOfDays = totalTime % 24000;
        long hours = ((timeOfDays + 6000) / 1000) % 24;
        long minutes = ((timeOfDays % 1000) * 60) / 1000;

        return I18n.translate("quickinfo.days", realDays, hours, minutes);
//        return String.format("第%d天，%02d:%02d", realDays, hours, minutes);
    }

    private World getWorld() {
        return DataFixUtils.orElse(Optional.ofNullable(this.client.getServer()).map((integratedServer) ->{
            return integratedServer.getWorld(this.client.world.dimension.getType());
        }), this.client.world);
    }

    @Nullable
    private WorldChunk getChunk() {
        if (null == this.chunkFuture) {
            IntegratedServer integratedServer = this.client.getServer();
            if (null != integratedServer) {
                ServerWorld serverWorld = integratedServer.getWorld(this.client.world.dimension.getType());
                if (null != serverWorld && null != this.chunkPos) {
                    this.chunkFuture = serverWorld.method_16177(this.chunkPos.x, this.chunkPos.z, false);
                }
            }
        }
        if (null == this.chunkFuture) {
            this.chunkFuture = CompletableFuture.completedFuture(this.getClientChunk());
        }

        return this.chunkFuture.getNow(null);
    }

    private WorldChunk getClientChunk() {
        if (null == this.chunkClient && null != this.chunkPos) {
            this.chunkClient = this.client.world.getWorldChunk(this.chunkPos.x, this.chunkPos.z);
        }

        return this.chunkClient;
    }
}
