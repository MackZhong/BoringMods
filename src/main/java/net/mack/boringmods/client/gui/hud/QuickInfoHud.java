package net.mack.boringmods.client.gui.hud;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.datafixers.DataFixUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.mack.boringmods.client.options.ModConfigs;
import net.mack.boringmods.impl.QuickInfo;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.fluid.BaseFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.LavaFluid;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.IntegerProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.tag.TagManager;
import net.minecraft.text.TextFormat;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.LightType;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkPos;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.chunk.light.LightingProvider;

import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;

@Environment(EnvType.CLIENT)
public class QuickInfoHud extends DrawableHelper {
    private final MinecraftClient client;
    private final TextRenderer fontRenderer;
    private HitResult hitFluid;
    private Entity player;
    @Nullable
    private WorldChunk chunkClient;
    @Nullable
    private CompletableFuture<WorldChunk> chunkFuture;
    @Nullable
    private ChunkPos chunkPos;
//    @Nullable
//    private ChunkNibbleArray lightingArray;

    public QuickInfoHud(MinecraftClient mcClient) {
        this.client = mcClient;
        this.fontRenderer = mcClient.textRenderer;
        ModConfigs.LOGGER.info("QuickInfo Hud initialized.");
    }

    private void resetChunk() {
        this.chunkFuture = null;
        this.chunkClient = null;
//        this.logger.info("Reset Chunk.");
    }

    public void draw() {
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
//
//    private void drawLighting() {
//        if (null == this.lightingArray)
//            return;
//        for (Byte l : this.lightingArray.asByteArray()) {
//            Byte b = l;
//        }
//    }

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
        int left = (scaleWidth - maxLineWidth) / 2 - 2;
        fill(left, top, left + maxLineWidth + 1, top + lines.size() * lineHeight + 2, 0x88B0B0B0);
        top++;
        left++;
        maxLineWidth--;
        for (String line : lines) {
            fill(left, top, left + maxLineWidth, top + lineHeight, 0xAA0000AA);
            this.fontRenderer.draw(line, left + 1, top + 1, 0x00E0E0E0);
            top += lineHeight;
        }
    }

    private List<String> getInfos() {
        List<String> infos = Lists.newArrayList();
        if (this.client.hasReducedDebugInfo()) {
            infos.add("Debug Info");
            return infos;
        }

        BlockPos playerPos = this.player.getBlockPos();
        playerPos = new BlockPos(playerPos.getX(), (int) this.player.getBoundingBox().minY, playerPos.getZ());
        Direction facing = this.player.getHorizontalFacing();
        infos.add(String.format("%d, %d, %d %s",
                playerPos.getX(), playerPos.getY(), playerPos.getZ(),
                I18n.translate("quickinfo." + facing.asString())));

        infos.add(getTimeDesc());
//        ChunkPos posChunk = new ChunkPos(pos);
//        if (!Objects.equals(this.chunkPos, posChunk)) {
//            this.chunkPos = posChunk;
//            this.resetChunk();
//        }
//        World world = this.getWorld();
        infos.add(TextFormat.GOLD + this.client.world.getBiome(playerPos).getTextComponent().getFormattedText());

        ClientPlayNetworkHandler net = this.client.getNetworkHandler();
        TagManager tagManager = null;
        if (null != net) {
            tagManager = net.getTagManager();
        }

        StringBuilder displayName = new StringBuilder();

        // Entity
        if (this.client.hitResult instanceof EntityHitResult) {
            EntityHitResult entityResult = (EntityHitResult)this.client.hitResult;
            Entity target = entityResult.getEntity();// this.client.targetedEntity;
            TextFormat format = TextFormat.GRAY;
            if (target instanceof Monster) {
                format = TextFormat.RED;
            } else if (target instanceof PassiveEntity) {
                format = TextFormat.GREEN;
            }

            displayName
                    .append(format)
                    .append(target.getName().getFormattedText())
                    .append("/")
                    .append(format)
                    .append(target.getDisplayName().getFormattedText());
            if (target instanceof LivingEntity) {
                LivingEntity living = (LivingEntity) target;
                displayName
                        .append(TextFormat.GRAY)
                        .append(String.format("(%2.1f/%2.1f)", living.getHealth(), living.getHealthMaximum()));
            }
            infos.add(displayName.toString());

            if (null != target.getCustomName())
                infos.add("CustomName: " + format + target.getCustomName().getFormattedText());
            if (QuickInfo.INSTANCE.detail())
                infos.add(TextFormat.WHITE + String.valueOf(Registry.ENTITY_TYPE.getId(target.getType())));
        } else if (this.client.hitResult instanceof BlockHitResult) {
            BlockPos targetPos = ((BlockHitResult) this.client.hitResult).getBlockPos();
            BlockState blockState = this.client.world.getBlockState(targetPos);
            // Block
            if (!blockState.isAir()) {
                Block block = blockState.getBlock();
                displayName
                        .append(block.getTextComponent().getFormattedText());

                for (Property<?> property : blockState.getProperties()) {
                    if (property.getName().equals("age")) {
                        IntegerProperty keyAge = (IntegerProperty) property;
//                    Object[] values = key.getValues().toArray();
                        Integer maxAge = 0;
                        for (Integer v : keyAge.getValues()) {
                            if (v > maxAge) {
                                maxAge = v;
                            }
                        }

                        Integer ageValue = blockState.get(keyAge);
                        TextFormat format2;
                        if (ageValue.equals(maxAge)) {
                            format2 = TextFormat.GOLD;
                        } else {
                            format2 = TextFormat.GREEN;
                        }
                        displayName
                                .append(format2)
                                .append(String.format("(%d/%d)", ageValue, maxAge));
                    }
                }
                infos.add(displayName.toString());

                if (QuickInfo.INSTANCE.detail()) {
                    infos.add(TextFormat.WHITE + String.valueOf(Registry.BLOCK.getId(block)) +
                            "/" + block.getRenderLayer().name());

                    // Lighting
                    if (((BlockHitResult) this.client.hitResult).getSide() == Direction.UP) {
                        BlockPos upPos = targetPos.up();
                        ChunkPos posChunk = new ChunkPos(upPos);
                        if (!Objects.equals(this.chunkPos, posChunk)) {
                            this.chunkPos = posChunk;
                            this.resetChunk();
                        }
                        if (this.client.world.isBlockLoaded(upPos)) {
                            WorldChunk chunk = this.getClientChunk();
                            if (!chunk.isEmpty()) {
                                infos.add(I18n.translate("quickinfo.light.client",
                                        chunk.getLightLevel(upPos, 0), this.client.world.getLightLevel(LightType.SKY, upPos), this.client.world.getLightLevel(LightType.BLOCK, upPos)));
                                chunk = this.getChunk();
                                if (null != chunk) {
                                    World world = this.getWorld();
                                    LightingProvider provider = world.getChunkManager().getLightingProvider();
                                    infos.add(I18n.translate("quickinfo.light.server",
                                            provider.get(LightType.SKY).getLightLevel(upPos), provider.get(LightType.BLOCK).getLightLevel(upPos)));
//                            this.lightingArray = provider.get(LightType.BLOCK_LIGHT).getChunkLightArray(pos.getX(), pos.getY(), pos.getZ());
                                }
                            }
                        }
                    }

                    // properties
                    for (Property<?> property : blockState.getProperties()) {
//                        if (!property.getName().equals("age")) {
//                            IntegerProperty key = (IntegerProperty) property;
//                            Object[] values = key.getValues().toArray();
//                            Integer maxAge = 0;
//                            for (Object v : values) {
//                                if ((Integer) v > maxAge) {
//                                    maxAge = (Integer) v;
//                                }
//                            }
//                            Integer age = blockState.get(key);
//                            TextFormat format;
//                            if (maxAge.equals(age)) {
//                                format = TextFormat.GOLD;
//                            } else {
//                                format = TextFormat.GREEN;
//                            }
//                            infos.add(String.format(Locale.getDefault(), "%s%d/%d", format, age, maxAge));
//                        } else {
                            infos.add(TextFormat.AQUA + property.getName() + "=" + blockState.get(property));
                            infos.add(TextFormat.WHITE + property.getValues().toString());
//                        }
                    }

                    // tags
                    if (null != tagManager) {
                        for (Identifier id : tagManager.blocks().getTagsFor(block)) {
                            infos.add(String.format("#%s", id));
                        }
                    }
                }
            }
        }
        else{
            infos.add(TextFormat.RED + "Unknown HitResult.");
        }

        // Fluid
        if (null != this.hitFluid && this.hitFluid.getType() == HitResult.Type.BLOCK) {
            BlockPos fluidPos = ((BlockHitResult) this.hitFluid).getBlockPos();
            FluidState fluidState = this.client.world.getFluidState(fluidPos);
            BlockState fluidBlockState = fluidState.getBlockState();
            if (!fluidState.isEmpty()) {
                Fluid fluid = fluidState.getFluid();
                infos.add(TextFormat.UNDERLINE + String.valueOf(Registry.FLUID.getId(fluid)));
//                BlockState fluidBlockState = fluidState.getBlockState();
                infos.add(((fluid instanceof LavaFluid) ? TextFormat.RED : TextFormat.GRAY) +
                        fluidBlockState.getBlock().getTextComponent().getFormattedText());

                // properties
                Boolean falling = fluidState.get(BaseFluid.FALLING);
                infos.add(String.format("Falling: %b, Level: %d", falling, fluid.getLevel(fluidState)));
                for (Property<?> property : fluidBlockState.getProperties()) {
                    if (Properties.FALLING == property || Properties.FLUID_BLOCK_LEVEL == property || Properties.FLUID_LEVEL == property) {
                        continue;
                    }
                    infos.add(String.format("%s=%s", property.getName(), fluidBlockState.get(property)));
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
    }

    private World getWorld() {
//        return DataFixUtils.orElse(Optional.ofNullable(this.client.getServer()).map((integratedServer) -> integratedServer.getWorld(this.client.world.dimension.getType())), this.client.world);
        return DataFixUtils.orElse(Optional.ofNullable(this.client.getServer()).map((integratedServer_1) -> {
            return integratedServer_1.getWorld(this.client.world.dimension.getType());
        }), this.client.world);
    }

    @Nullable
    private WorldChunk getChunk() {
        if (null == this.chunkFuture) {
            IntegratedServer integratedServer = this.client.getServer();
            if (null != integratedServer) {
                ServerWorld serverWorld = integratedServer.getWorld(this.client.world.dimension.getType());
                if (null != serverWorld && null != this.chunkPos) {
                    this.chunkFuture = serverWorld.getChunkFutureSyncOnMainThread(this.chunkPos.x, this.chunkPos.z, false);
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
            this.chunkClient = this.client.world.method_8497(this.chunkPos.x, this.chunkPos.z);
        }

        return this.chunkClient;
    }
}
