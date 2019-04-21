package net.mack.boringmods.impl;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.mack.boringmods.client.options.HarvestConfig;
import net.mack.boringmods.client.options.ModConfigs;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.io.*;
import java.util.List;

public class Harvest implements UseBlockCallback {

    public final static Harvest INSTANCE = new Harvest();

    private HarvestConfig config;

    public void onInitialize() {
        File configFile = new File(FabricLoader.getInstance().getConfigDirectory(), "harvest.json");
        try (FileReader reader = new FileReader(configFile)) {
            this.config = new Gson().fromJson(reader, HarvestConfig.class);
            if (null != this.config)
                ModConfigs.LOGGER.info("[BoringMods]Successfully loaded harvest config.");
        } catch (Exception ioe1) {
            ModConfigs.LOGGER.error("[BoringMods]Failed to read from Harvest config file, generating a default one.\n", ioe1);
            ioe1.printStackTrace();
        }

        if (null == this.config) {
            this.config = new HarvestConfig();
            try (FileWriter writer = new FileWriter(configFile)) {
                GsonBuilder builder = new GsonBuilder();
                Gson json = builder.setPrettyPrinting().create();
                writer.write(json.toJson(this.config));
            } catch (Exception ioe2) {
                ModConfigs.LOGGER.error("[BoringMods]Failed to generate new Harvest config file.\n", ioe2);
                ioe2.printStackTrace();
            }
        }

        UseBlockCallback.EVENT.register(this);
    }

    @Override
    public ActionResult interact(PlayerEntity playerEntity, World world, Hand hand, BlockHitResult blockHitResult) {
        if (!(world instanceof ServerWorld) || playerEntity.isCreative()) {
            return ActionResult.PASS;
        }
        BlockPos pos = blockHitResult.getBlockPos();
        BlockState state = world.getBlockState(pos);
        HarvestCrop crop = config.getCrops().stream().filter(c -> c.test(state)).findFirst().orElse(null);
        if (null == crop) {
            return ActionResult.PASS;
        }

        ItemStack handled = playerEntity.getStackInHand(hand);
        if (handled.isEmpty() || handled.getItem() != crop.getSeed()) {
            return ActionResult.PASS;
        }

        BlockEntity plant = world.getBlockEntity(pos);
        List<ItemStack> drops = Block.getDroppedStacks(state, (ServerWorld) world, pos, plant, playerEntity, handled);
        boolean seedFound = false;
        ItemStack seeds = null;
        for (ItemStack drop : drops) {
            if (crop.getSeed() == drop.getItem()) {
                seedFound = true;
                seeds = drop;
                break;
            }
        }

        if (seedFound) {
            world.setBlockState(pos, Blocks.AIR.getDefaultState());
            drops.forEach(drop -> ItemScatterer.spawn(world, pos.getX(), pos.getY(), pos.getZ(), drop));

            BlockHitResult newHitResult = new BlockHitResult(blockHitResult.getPos(), Direction.UP, blockHitResult.getBlockPos().down(), true);
            ItemUsageContext context = new ItemUsageContext(playerEntity, hand, newHitResult);
            return seeds.useOnBlock(context);
        }

        return ActionResult.FAIL;
    }
}
