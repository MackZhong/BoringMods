package net.mack.boringmods.impl;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.fabric.api.client.keybinding.FabricKeyBinding;
import net.fabricmc.fabric.impl.client.keybinding.KeyBindingRegistryImpl;
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
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.LightType;
import net.minecraft.world.World;

import java.awt.*;

public class LightOverlay {
    private boolean enabled = false;
    private FabricKeyBinding keyToggleLightOverlay;
    private int range = 12;

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
//        return LightOverlay.range;
//    }
//
//    private static boolean isEnabled() {
//        return enabled;
//    }
//
//    public static void setEnabled(boolean enabled) {
//        LightOverlay.enabled = enabled;
//    }

    private void toggle() {
        this.enabled = !this.enabled;
//        return LightOverlay.enabled;
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
        if (blockBelowState.isAir() || !world.getBlockState(pos).isAir() || !blockBelowState.hasSolidTopSurface(world, pos) || !world.getFluidState(pos.down()).isEmpty())
            return OverlayType.NONE;
        if (world.method_8312(LightType.BLOCK, pos) >= 8)
            return OverlayType.NONE;
        if (world.method_8312(LightType.SKY, pos) >= 8)
            return OverlayType.WARNING;
        return OverlayType.DANGEROUS;
    }

    public void render(World world, PlayerEntity playerEntity) {
        if (this.enabled) {
            GlStateManager.disableTexture();
            GlStateManager.disableBlend();
            BlockPos playerPos = playerEntity.getPos();//new BlockPos(playerEntity.x, playerEntity.y, playerEntity.z);
            BlockPos.iterateBoxPositions(playerPos.add(-range, -range, -range), playerPos.add(range, range, range)).forEach(pos -> {
                if (world.getBiome(pos).getMaxSpawnLimit() > 0) {
                    OverlayType type = getOverlayType(pos, world, playerEntity);
                    if (type != OverlayType.NONE) {
//                        VoxelShape shape = world.getBlockState(pos).getCollisionShape(world, pos);
                        Color color = type == OverlayType.DANGEROUS ? Color.RED : Color.YELLOW;
                        this.renderOverlay(pos, color);
                    }
                }
            });
            GlStateManager.enableBlend();
            GlStateManager.enableTexture();
        }
    }

    private void renderOverlay(BlockPos pos, Color color) {
        Camera camera = MinecraftClient.getInstance().gameRenderer.method_19418();
        GlStateManager.lineWidth(1.0F);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBufferBuilder();
        Vec3d vecCamera = camera.getPos();
        double d0 = vecCamera.x;
        double d1 = vecCamera.y - .005D;
        double d2 = vecCamera.z;

        buffer.begin(1, VertexFormats.POSITION_COLOR);
        buffer.vertex(pos.getX() - d0, pos.getY() - d1, pos.getZ() - d2).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).next();
        buffer.vertex(pos.getX() + 1 - d0, pos.getY() - d1, pos.getZ() + 1 - d2).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).next();
        buffer.vertex(pos.getX() + 1 - d0, pos.getY() - d1, pos.getZ() - d2).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).next();
        buffer.vertex(pos.getX() - d0, pos.getY() - d1, pos.getZ() + 1 - d2).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).next();
        tessellator.draw();
    }
}
