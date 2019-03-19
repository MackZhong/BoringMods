package net.mack.boringmods.impl;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.fabric.api.client.keybinding.FabricKeyBinding;
import net.fabricmc.fabric.impl.client.keybinding.KeyBindingRegistryImpl;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.class_4184;
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
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.LightType;
import net.minecraft.world.World;

import java.awt.*;

public class LightOverlay {
    private static boolean enabled = false;
    private static FabricKeyBinding keyToggleLightOverlay;
    private static int range = 12;

    enum OverlayType {
        NONE, WARNING, DANGERIOUS
    }

    public static void keyBinding(String category) {
        keyToggleLightOverlay = FabricKeyBinding.Builder.create(
                new Identifier("boringmods:toggle_light_overlay"),
                InputUtil.Type.KEY_KEYBOARD,
                296,
                category
        ).build();
        KeyBindingRegistryImpl.INSTANCE.register(keyToggleLightOverlay);
    }

    public static int getRange() {
        return LightOverlay.range;
    }

    public static boolean isEnabled() {
        return enabled;
    }

    public static void setEnabled(boolean enabled) {
        LightOverlay.enabled = enabled;
    }

    public static boolean toogle() {
        LightOverlay.enabled = !LightOverlay.enabled;
        return LightOverlay.enabled;
    }

    public static FabricKeyBinding getKeyToggleLightOverlay() {
        return keyToggleLightOverlay;
    }

    public static void handleInput() {
        while (LightOverlay.keyToggleLightOverlay.wasPressed()) {
            LightOverlay.toogle();
        }
    }

    public static OverlayType getOverlayType(BlockPos pos, World world, PlayerEntity playerEntity) {
        BlockState blockBelowState = world.getBlockState(pos.down());
        if (blockBelowState.getBlock() == Blocks.BEDROCK || blockBelowState.getBlock() == Blocks.BARRIER)
            return OverlayType.NONE;
        if ((!blockBelowState.getMaterial().method_15804() && blockBelowState.isTranslucent(world, pos.down())) || !SpawnHelper.isClearForSpawn(world, pos, world.getBlockState(pos), world.getFluidState(pos)))
            return OverlayType.NONE;
        if (!world.method_8628(world.getBlockState(pos), pos, VerticalEntityPosition.fromEntity(playerEntity)))
            return OverlayType.NONE;
        if (blockBelowState.isAir() || !world.getBlockState(pos).isAir() || !blockBelowState.hasSolidTopSurface(world, pos) || !world.getFluidState(pos.down()).isEmpty())
            return OverlayType.NONE;
        if (world.method_8312(LightType.BLOCK, pos) >= 8)
            return OverlayType.NONE;
        if (world.method_8312(LightType.SKY, pos) >= 8)
            return OverlayType.WARNING;
        return OverlayType.DANGERIOUS;
    }


    public static void render(World world, PlayerEntity playerEntity) {
        if (isEnabled()) {
            GlStateManager.disableTexture();
            GlStateManager.disableBlend();
            BlockPos playerPos = playerEntity.getPos();//new BlockPos(playerEntity.x, playerEntity.y, playerEntity.z);
            BlockPos.iterateBoxPositions(playerPos.add(-range, -range, -range), playerPos.add(range, range, range)).forEach(pos -> {
                if (world.getBiome(pos).getMaxSpawnLimit() > 0) {
                    OverlayType type = getOverlayType(pos, world, playerEntity);
                    if (type != OverlayType.NONE) {
                        VoxelShape shape = world.getBlockState(pos).getCollisionShape(world, pos);
                        Color color = type == OverlayType.DANGERIOUS ? Color.RED : Color.YELLOW;
                        renderOverlay(pos, color);
                    }
                }
            });
            GlStateManager.enableBlend();
            GlStateManager.enableTexture();
        }
    }

    public static void renderOverlay(BlockPos pos, Color color) {
        class_4184 class_4184 = MinecraftClient.getInstance().gameRenderer.method_19418();
        GlStateManager.lineWidth(1.0F);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBufferBuilder();
        double d0 = class_4184.method_19326().x;
        double d1 = class_4184.method_19326().y - .005D;
        double d2 = class_4184.method_19326().z;

        buffer.begin(1, VertexFormats.POSITION_COLOR);
        buffer.vertex(pos.getX() - d0, pos.getY() - d1, pos.getZ() - d2).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).next();
        buffer.vertex(pos.getX() + 1 - d0, pos.getY() - d1, pos.getZ() + 1 - d2).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).next();
        buffer.vertex(pos.getX() + 1 - d0, pos.getY() - d1, pos.getZ() - d2).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).next();
        buffer.vertex(pos.getX() - d0, pos.getY() - d1, pos.getZ() + 1 - d2).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).next();
        tessellator.draw();
    }
}
