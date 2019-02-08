package net.mack.boringmods.mixin;

import net.minecraft.block.*;
import net.minecraft.server.network.ServerPlayerInteractionManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

@Mixin(value = ServerPlayerInteractionManager.class)
public abstract class MixinServerPlayerInteractionManager {
    private org.apache.logging.log4j.Logger logger = org.apache.logging.log4j.LogManager.getLogger("boringmods");
    private static List<BlockPos> blockPosWaiting = new ArrayList<BlockPos>();
    private static Vec3i[] neighborPos = {
            new Vec3i(0, 0, -1),
            new Vec3i(-1, 0, -1),
            new Vec3i(-1, 0, 0),
            new Vec3i(-1, 0, 1),
            new Vec3i(0, 0, 1),
            new Vec3i(1, 0, 1),
            new Vec3i(1, 1, 1),
            new Vec3i(0, 1, 1),
            new Vec3i(-1, 1, 1),
            new Vec3i(-1, 1, 0),
            new Vec3i(-1, 1, -1),
            new Vec3i(0, 1, -1),
            new Vec3i(0, 1, 0),
            new Vec3i(1, 1, 0),
            new Vec3i(1, 1, -1),
            new Vec3i(1, 0, -1),
            new Vec3i(1, 0, 0),
            new Vec3i(0, -1, 0),
            new Vec3i(0, -1, -1),
            new Vec3i(0, -1, 1),
            new Vec3i(-1, -1, 0),
            new Vec3i(1, -1, 0),
            new Vec3i(-1, -1, -1),
            new Vec3i(1, -1, -1),
            new Vec3i(-1, -1, 1),
            new Vec3i(1, -1, 1)
    };

    @Shadow
    public abstract boolean tryBreakBlock(BlockPos blockPos_1);

    @Shadow
    public World world;

    @Inject(method = "update"
            , at = @At(value = "HEAD")
    )
    private void onUpdate(CallbackInfo ci) {
        if (blockPosWaiting.size() > 0) {
            this.tryBreakBlock(blockPosWaiting.remove(0));
        }
    }

    @Redirect(method = "method_14258"
            , at = @At(value = "INVOKE"
//            , args = "log=true"
            , target = "Lnet/minecraft/server/network/ServerPlayerInteractionManager;tryBreakBlock(Lnet/minecraft/util/math/BlockPos;)Z")
    )
    private boolean onBreakBlock(ServerPlayerInteractionManager manager, BlockPos pos) {
        BlockState state = this.world.getBlockState(pos);
        Block block = state.getBlock();
        if (block instanceof OreBlock || block instanceof RedstoneOreBlock) {
            blockPosWaiting.clear();
            getNeighborBlocks(pos, block, 26);
        } else if (block instanceof LogBlock) {
            blockPosWaiting.clear();
            getNeighborBlocks(pos, block, 17);
        }

        return this.tryBreakBlock(pos);
    }

    private void getNeighborBlocks(BlockPos pos, Block block, int neighbors) {
        blockPosWaiting.add(pos);
        if (neighbors > neighborPos.length) {
            neighbors = neighborPos.length;
        }
        for (int i = 0; i < neighbors; ++i) {
            BlockPos nextPos = pos.add(neighborPos[i]);
            BlockState state = this.world.getBlockState(nextPos);
            if (!state.isAir() && state.getBlock() == block && !blockPosWaiting.contains(nextPos)) {
                getNeighborBlocks(nextPos, block, neighbors);
            }
        }
    }
}
