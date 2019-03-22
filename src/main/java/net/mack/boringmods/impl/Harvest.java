package net.mack.boringmods.impl;

import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.tag.TagRegistry;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sortme.ItemScatterer;
import net.minecraft.tag.Tag;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.io.File;
import java.util.List;

public class Harvest implements UseBlockCallback {
    private org.apache.logging.log4j.Logger logger = org.apache.logging.log4j.LogManager.getLogger("boringmods");

    public final static Harvest INSTANCE = new Harvest();

    private HarvestConfig config;
    private final Tag<Item> SEEDS_TAG = TagRegistry.item(new Identifier("boringmods", "seeds"));

    private ActionResult plant(ServerWorld world, BlockHitResult hitResult, BlockState state, PlayerEntity playerEntity, BlockEntity blockEntity){
        HarvestCrop crop = config.getCrops().stream().filter(c->c.test(state)).findFirst().orElse(null);
        if (null==crop){
            return ActionResult.PASS;
        }

        BlockPos pos = hitResult.getBlockPos();
        List<ItemStack> drops = Block.getDroppedStacks(state, world, pos, blockEntity, playerEntity, playerEntity.getStackInHand(Hand.MAIN));
        boolean seedFound = false;
        for (ItemStack drop : drops){
            if (SEEDS_TAG.contains(drop.getItem())){
                seedFound = true;
                drop.subtractAmount(1);
                break;
            }
        }

        if (seedFound) {
            drops.forEach((stack) -> {
                ItemScatterer.spawn(world, pos.getX(), pos.getY(), pos.getZ(), stack);
            });
            world.setBlockState(pos, state.getBlock().getDefaultState());
            return ActionResult.SUCCESS;
        } else {
            return ActionResult.FAIL;
        }
    }

    public void onInitialize(){
        this.config = new HarvestConfig();
        File configFile = new File(FabricLoader.getInstance().getConfigDirectory(), "harvest.json");
//        try(FileWriter writer = new FileWriter(configFile)){
//            writer.write(new GsonBuilder().setPrettyPrinting().create().toJson(this.config));
//        }
//        catch (IOException ioe2){
//            this.logger.error("Failed to generate new Harvest config file.\n{}", ioe2);
//        }

        UseBlockCallback.EVENT.register(this);
    }

    @Override
    public ActionResult interact(PlayerEntity playerEntity, World world, Hand hand, BlockHitResult blockHitResult) {
        if (!(world instanceof ServerWorld)){
            return ActionResult.PASS;
        }
        if (Hand.MAIN != hand){
            return ActionResult.PASS;
        }

        BlockPos pos = blockHitResult.getBlockPos();
        BlockState state = world.getBlockState(pos);
        ActionResult result = this.plant((ServerWorld)world, blockHitResult, state, playerEntity, world.getBlockEntity(pos));
        if (ActionResult.SUCCESS == result){
            playerEntity.swingHand(hand);
            playerEntity.addExhaustion(this.config.getExhaustionPerHarvest());
        }

        return result;
    }
}
