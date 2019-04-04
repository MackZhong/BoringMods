package net.mack.boringmods.impl;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.fabric.api.client.keybinding.FabricKeyBinding;
import net.mack.boringmods.client.options.ModOption;
import net.mack.boringmods.client.options.ModOptions;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.VerticalEntityPosition;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sortme.SpawnHelper;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.LightType;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.dimension.DimensionType;

import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

public class LightOverlay {
    private FabricKeyBinding keyToggleLightOverlay;
    private Table<Integer, Integer, Boolean> slimeTable = HashBasedTable.create();

    enum OverlayType {
        NONE, WARNING, DANGEROUS
    }

    private final static LightOverlay instance = new LightOverlay();

    public static LightOverlay getInstance() {
        return instance;
    }

    public FabricKeyBinding getKeyBinding(String category) {
        this.keyToggleLightOverlay = FabricKeyBinding.Builder.create(
                new Identifier("boringmods:toggle_light_overlay"),
                InputUtil.Type.KEY_KEYBOARD,
                296,
                category
        ).build();
        return this.keyToggleLightOverlay;
    }
//
//    public static int getRange() {
//        return LightOverlay.lightOverlayRange;
//    }
//
//    private static boolean isEnabled() {
//        return lightOverlayEnabled;
//    }
//
//    public static void setEnabled(boolean lightOverlayEnabled) {
//        LightOverlay.lightOverlayEnabled = lightOverlayEnabled;
//    }

    private void toggle() {
        ModOption.LIGHT_OVERLAY_ENABLE.toggle(ModOptions.INSTANCE);
    }
//
//    public static FabricKeyBinding getKeyToggleLightOverlay() {
//        return keyToggleLightOverlay;
//    }

    public void handleInput() {
        while (this.keyToggleLightOverlay.wasPressed()) {
            this.toggle();
        }
    }

    private OverlayType getOverlayType(BlockPos pos, World world, PlayerEntity playerEntity) {
        BlockState blockBelowState = world.getBlockState(pos.down());
        if (blockBelowState.getBlock() == Blocks.BEDROCK || blockBelowState.getBlock() == Blocks.BARRIER)
            return OverlayType.NONE;
        if ((!blockBelowState.getMaterial().blocksLight() && blockBelowState.isTranslucent(world, pos.down())) || !SpawnHelper.isClearForSpawn(world, pos, world.getBlockState(pos), world.getFluidState(pos)))
            return OverlayType.NONE;
        if (!world.canPlace(world.getBlockState(pos), pos, VerticalEntityPosition.fromEntity(playerEntity)))
            return OverlayType.NONE;
        if (blockBelowState.isAir() || !world.getBlockState(pos).isAir() || !blockBelowState.hasSolidTopSurface(world, pos, playerEntity) || !world.getFluidState(pos.down()).isEmpty())
            return OverlayType.NONE;
        if (world.getLightLevel(LightType.BLOCK, pos) >= 8)
            return OverlayType.NONE;
        if (world.getLightLevel(LightType.SKY, pos) >= 8)
            return OverlayType.WARNING;
        return OverlayType.DANGEROUS;
    }

    private boolean canSlimeSpawn(long seed, World world, BlockPos pos, PlayerEntity playerEntity) {
        Biome biome = world.getBiome(pos);
        if (Biomes.SWAMP == biome || Biomes.SWAMP_HILLS == biome) {
            return true;
        }
        if (Biomes.MUSHROOM_FIELD_SHORE == biome || Biomes.MUSHROOM_FIELDS == biome) {
            return false;
        }
        long xPosition = pos.getX() >> 4;
        long zPosition = pos.getZ() >> 4;
        BlockState blockBelowState = world.getBlockState(pos.down());
        Block block = blockBelowState.getBlock();
        if (Blocks.BEDROCK == block ||
                Blocks.BARRIER == block ||
                !world.canPlace(blockBelowState, pos, VerticalEntityPosition.fromEntity(playerEntity)) ||
                !SpawnHelper.isClearForSpawn(world, pos, world.getBlockState(pos), world.getFluidState(pos)) ||
//                blockBelowState.isAir() ||
//                !world.getBlockState(pos).isAir() ||
//                pos.getY() > 39 ||
                !blockBelowState.hasSolidTopSurface(world, pos, playerEntity))
            return false;

        Integer xArea = (int) xPosition;
        Integer zArea = (int) zPosition;
        if (this.slimeTable.contains(xArea, zArea))
            return true;

        Random rnd = new Random(seed +
                (long) (xPosition * xPosition * 0x4c1906) +
                (long) (xPosition * 0x5ac0db) +
                (long) (zPosition * zPosition) * 0x4307a7L +
                (long) (zPosition * 0x5f24f) ^ 0x3ad8025f);
        boolean slimeSpawn = rnd.nextInt(10) == 0;
        if (slimeSpawn) {
            this.slimeTable.put(xArea, zArea, true);
        }
        return slimeSpawn;
    }

    public void render(World world, PlayerEntity playerEntity) {
        if (ModOption.LIGHT_OVERLAY_ENABLE.getValue(ModOptions.INSTANCE)) {
            GlStateManager.disableTexture();
            GlStateManager.disableBlend();
            GlStateManager.depthMask(false);

            int lightOverlayRange = ModOption.LIGHT_OVERLAY_RANGE.getValue(ModOptions.INSTANCE).intValue();
            BlockPos playerPos = playerEntity.getBlockPos();
            Camera camera = MinecraftClient.getInstance().gameRenderer.getCamera();
            Vec3d vecCamera = camera.getPos();

            boolean overWorld = world.getDimension().getType() == DimensionType.OVERWORLD;
            MinecraftServer server = MinecraftClient.getInstance().getServer();
            if (overWorld && null != server) {
                ServerWorld serverWorld = server.getWorld(DimensionType.OVERWORLD);
                long seed = serverWorld.getSeed();
                ArrayList<BlockPos> slimeBlocks = new ArrayList<>();
                BlockPos.iterateBoxPositions(playerPos.add(-lightOverlayRange, -lightOverlayRange, -lightOverlayRange),
                        playerPos.add(lightOverlayRange, 3, lightOverlayRange)).forEach(pos -> {
                    if (world.getBiome(pos).getMaxSpawnLimit() > 0 &&
                            this.canSlimeSpawn(seed, world, pos, playerEntity)) {
                        slimeBlocks.add(new BlockPos(pos));
//                        this.renderSlime(vecCamera, pos);
                    }
                });

                GlStateManager.lineWidth(2.0F);
                this.renderSlimeBlocks(vecCamera, slimeBlocks);
            }

            ArrayList<BlockPos> dangerousBlocks = new ArrayList<>();
            BlockPos.iterateBoxPositions(playerPos.add(-lightOverlayRange, -lightOverlayRange, -lightOverlayRange),
                    playerPos.add(lightOverlayRange, 3, lightOverlayRange)).forEach(pos -> {
                if (world.getBiome(pos).getMaxSpawnLimit() > 0) {
                    OverlayType type = this.getOverlayType(pos, world, playerEntity);
                    if (type != OverlayType.NONE) {
//                        VoxelShape shape = world.getBlockState(pos).getCollisionShape(world, pos);
                        Color color = type == OverlayType.DANGEROUS ? Color.RED : Color.YELLOW;
//                        this.renderOverlay(vecCamera, pos, color);
                        dangerousBlocks.add(new BlockPos(pos));
                    }
                }
            });

            GlStateManager.lineWidth(1.0F);
            this.renderDangerousBlocks(vecCamera, dangerousBlocks);

            GlStateManager.depthMask(true);
            GlStateManager.enableBlend();
            GlStateManager.enableTexture();
        }
    }

    private void renderDangerousBlocks(Vec3d vecCamera, ArrayList<BlockPos> dangerousBlocks) {
        double d0 = vecCamera.x;
        double d1 = vecCamera.y - 0.02D;
        double d2 = vecCamera.z;
        Color color = Color.RED;

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBufferBuilder();
        buffer.begin(1, VertexFormats.POSITION_COLOR);
        for (BlockPos pos : dangerousBlocks) {
            buffer.vertex(pos.getX() + 0.1 - d0, pos.getY() - d1, pos.getZ() + 0.1 - d2).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).next();
            buffer.vertex(pos.getX() + 0.9 - d0, pos.getY() - d1, pos.getZ() + 0.9 - d2).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).next();
            buffer.vertex(pos.getX() + 0.9 - d0, pos.getY() - d1, pos.getZ() + 0.1 - d2).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).next();
            buffer.vertex(pos.getX() + 0.1 - d0, pos.getY() - d1, pos.getZ() + 0.9 - d2).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).next();
        }
        tessellator.draw();
    }

    private void renderSlimeBlocks(Vec3d vecCamera, ArrayList<BlockPos> slimeBlocks) {
        double d0 = vecCamera.x;
        double d1 = vecCamera.y - 0.005D;
        double d2 = vecCamera.z;
        Color color = Color.BLUE;

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBufferBuilder();
        buffer.begin(4, VertexFormats.POSITION_COLOR);
        for (BlockPos pos : slimeBlocks) {
//            buffer.vertex(pos.getX() - d0 + 1, pos.getY() - d1, pos.getZ() - d2).color(color.getRed(), color.getGreen(), color.getBlue(), 50).next();
            buffer.vertex(pos.getX() - d0, pos.getY() - d1, pos.getZ() - d2 + 1).color(color.getRed(), color.getGreen(), color.getBlue(), 50).next();
            buffer.vertex(pos.getX() - d0 + 1, pos.getY() - d1, pos.getZ() - d2 + 1).color(color.getRed(), color.getGreen(), color.getBlue(), 50).next();
            buffer.vertex(pos.getX() - d0 + 1, pos.getY() - d1, pos.getZ() - d2).color(color.getRed(), color.getGreen(), color.getBlue(), 50).next();
        }
        tessellator.draw();
    }

    private void renderOverlay(Vec3d vecCamera, BlockPos pos, Color color) {
        GlStateManager.lineWidth(1.0F);
        GlStateManager.depthMask(false);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBufferBuilder();
        double d0 = vecCamera.x;
        double d1 = vecCamera.y - .02D;
        double d2 = vecCamera.z;

        buffer.begin(1, VertexFormats.POSITION_COLOR);
        buffer.vertex(pos.getX() + 0.2 - d0, pos.getY() - d1, pos.getZ() + 0.2 - d2).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).next();
        buffer.vertex(pos.getX() + 0.8 - d0, pos.getY() - d1, pos.getZ() + 0.8 - d2).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).next();
        buffer.vertex(pos.getX() + 0.8 - d0, pos.getY() - d1, pos.getZ() + 0.2 - d2).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).next();
        buffer.vertex(pos.getX() + 0.2 - d0, pos.getY() - d1, pos.getZ() + 0.8 - d2).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).next();
        tessellator.draw();
        GlStateManager.depthMask(true);
    }

    private void renderSlime(Vec3d vecCamera, BlockPos pos) {
        GlStateManager.lineWidth(2.0F);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBufferBuilder();
        double d0 = vecCamera.x;
        double d1 = vecCamera.y - 0.005D;
        double d2 = vecCamera.z;
//        Color green = Color.GREEN;
        Color blue = Color.BLUE;

        buffer.begin(3, VertexFormats.POSITION_COLOR);
//        buffer.vertex(pos.getX() - d0, pos.getY() - d1, pos.getZ() - d2).color(green.getRed(), green.getGreen(), green.getBlue(), 20).next();
//        buffer.vertex(pos.getX() - d0, pos.getY() - d1, pos.getZ() - d2 + 1).color(green.getRed(), green.getGreen(), green.getBlue(), 50).next();
//        buffer.vertex(pos.getX() - d0 + 1, pos.getY() - d1, pos.getZ() - d2).color(green.getRed(), green.getGreen(), green.getBlue(), 100).next();
        buffer.vertex(pos.getX() - d0 + 1, pos.getY() - d1, pos.getZ() - d2).color(blue.getRed(), blue.getGreen(), blue.getBlue(), 50).next();
        buffer.vertex(pos.getX() - d0, pos.getY() - d1, pos.getZ() - d2 + 1).color(blue.getRed(), blue.getGreen(), blue.getBlue(), 50).next();
        buffer.vertex(pos.getX() - d0 + 1, pos.getY() - d1, pos.getZ() - d2 + 1).color(blue.getRed(), blue.getGreen(), blue.getBlue(), 50).next();
        buffer.vertex(pos.getX() - d0 + 1, pos.getY() - d1, pos.getZ() - d2).color(blue.getRed(), blue.getGreen(), blue.getBlue(), 50).next();
        tessellator.draw();
    }

}
