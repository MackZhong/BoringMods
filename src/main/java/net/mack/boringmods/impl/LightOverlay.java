package net.mack.boringmods.impl;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.fabric.api.client.keybinding.FabricKeyBinding;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.render.Camera;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.VerticalEntityPosition;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sortme.SpawnHelper;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.LightType;
import net.minecraft.world.World;

import java.awt.*;
import java.util.Random;

public class LightOverlay {
    private FabricKeyBinding keyToggleLightOverlay;

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
        this.lightOverlayEnabled = !this.lightOverlayEnabled;
//        return LightOverlay.lightOverlayEnabled;
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
        if (!world.method_8628(world.getBlockState(pos), pos, VerticalEntityPosition.fromEntity(playerEntity)))
            return OverlayType.NONE;
        if (blockBelowState.isAir() || !world.getBlockState(pos).isAir() || !blockBelowState.hasSolidTopSurface(world, pos, playerEntity) || !world.getFluidState(pos.down()).isEmpty())
            return OverlayType.NONE;
        if (world.method_8312(LightType.BLOCK, pos) >= 8)
            return OverlayType.NONE;
        if (world.method_8312(LightType.SKY, pos) >= 8)
            return OverlayType.WARNING;
        return OverlayType.DANGEROUS;
    }

    private boolean slimeSpawn(World world,  BlockPos pos, PlayerEntity playerEntity) {
        BlockState blockBelowState = world.getBlockState(pos.down());
        if (blockBelowState.isAir() || !world.getBlockState(pos).isAir() || !blockBelowState.hasSolidTopSurface(world, pos, playerEntity))
            return false;

        long xPosition = pos.getX() >> 4;
        long zPosition = pos.getZ() >> 4;
        Random rnd = new Random(world.getSeed() +
                (long) (xPosition * xPosition * 0x4c1906) +
                (long) (xPosition * 0x5ac0db) +
                (long) (zPosition * zPosition) * 0x4307a7L +
                (long) (zPosition * 0x5f24f) ^ 0x3ad8025f);
        return rnd.nextInt(10) == 0;
    }

    public void render(World world, PlayerEntity playerEntity) {
        if (this.lightOverlayEnabled) {
            GlStateManager.disableTexture();
            GlStateManager.disableBlend();
            BlockPos playerPos = playerEntity.getBlockPos();//new BlockPos(playerEntity.x, playerEntity.y, playerEntity.z);
            Camera camera = MinecraftClient.getInstance().gameRenderer.getCamera();
            Vec3d vecCamera = camera.getPos();
            BlockPos.iterateBoxPositions(playerPos.add(-lightOverlayRange, -lightOverlayRange, -lightOverlayRange), playerPos.add(lightOverlayRange, lightOverlayRange, lightOverlayRange)).forEach(pos -> {
                if (world.getBiome(pos).getMaxSpawnLimit() > 0) {
                    OverlayType type = this.getOverlayType(pos, world, playerEntity);
                    if (type != OverlayType.NONE) {
//                        VoxelShape shape = world.getBlockState(pos).getCollisionShape(world, pos);
                        Color color = type == OverlayType.DANGEROUS ? Color.RED : Color.YELLOW;
                        this.renderOverlay(vecCamera, pos, color);
                    }
                    if (this.slimeSpawn(world, pos, playerEntity)) {
                        this.renderSlime(vecCamera, pos);
                    }
                }
            });
            GlStateManager.enableBlend();
            GlStateManager.enableTexture();
        }
    }

    private void renderOverlay(Vec3d vecCamera, BlockPos pos, Color color) {
        GlStateManager.lineWidth(1.0F);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBufferBuilder();
        double d0 = vecCamera.x;
        double d1 = vecCamera.y - .005D;
        double d2 = vecCamera.z;

        buffer.begin(1, VertexFormats.POSITION_COLOR);
        buffer.vertex(pos.getX() + 0.2 - d0, pos.getY() - d1, pos.getZ() + 0.2 - d2).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).next();
        buffer.vertex(pos.getX() + 0.8 - d0, pos.getY() - d1, pos.getZ() + 0.8 - d2).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).next();
        buffer.vertex(pos.getX() + 0.8 - d0, pos.getY() - d1, pos.getZ() + 0.2 - d2).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).next();
        buffer.vertex(pos.getX() + 0.2 - d0, pos.getY() - d1, pos.getZ() + 0.8 - d2).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).next();
        tessellator.draw();
    }

    private void renderSlime(Vec3d vecCamera, BlockPos pos) {
        GlStateManager.lineWidth(2.0F);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBufferBuilder();
        double d0 = vecCamera.x;
        double d1 = vecCamera.y - 0.01D;
        double d2 = vecCamera.z;
        Color color = Color.GREEN;

        buffer.begin(1, VertexFormats.POSITION_COLOR);
        buffer.vertex(pos.getX() - d0, pos.getY() - d1, pos.getZ() - d2).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).next();
        buffer.vertex(pos.getX() + 1 - d0, pos.getY() - d1, pos.getZ() + 1 - d2).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).next();
        buffer.vertex(pos.getX() + 1 - d0, pos.getY() - d1, pos.getZ() - d2).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).next();
        buffer.vertex(pos.getX() - d0, pos.getY() - d1, pos.getZ() + 1 - d2).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).next();
        tessellator.draw();
    }

}
