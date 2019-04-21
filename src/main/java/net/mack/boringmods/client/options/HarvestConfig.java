package net.mack.boringmods.client.options;

import com.google.common.collect.Lists;
import net.mack.boringmods.impl.HarvestCrop;
import net.minecraft.block.Blocks;
import net.minecraft.item.Items;
import net.minecraft.state.property.Properties;

import java.util.List;

public class HarvestConfig {
    private float exhaustionPerHarvest;
    private boolean additionalLogging;
    private List<HarvestCrop> harvestCrops;

    public HarvestConfig() {
        this(0.005F, false, defaultCrops());
    }

    public HarvestConfig(float exhaustion, boolean logging, List<HarvestCrop> crops) {
        this.exhaustionPerHarvest = exhaustion;
        this.additionalLogging = logging;
        this.harvestCrops = crops;
    }

    public float getExhaustionPerHarvest(){
        return this.exhaustionPerHarvest;
    }

    public List<HarvestCrop> getCrops(){
        return this.harvestCrops;
    }

    private static List<HarvestCrop> defaultCrops() {
        return Lists.newArrayList(
                new HarvestCrop(Blocks.WHEAT.getDefaultState().with(Properties.AGE_7, 7), Items.WHEAT_SEEDS),
                new HarvestCrop(Blocks.NETHER_WART.getDefaultState().with(Properties.AGE_3, 3), Items.NETHER_WART),
                new HarvestCrop(Blocks.CARROTS.getDefaultState().with(Properties.AGE_7, 7), Items.CARROT),
                new HarvestCrop(Blocks.POTATOES.getDefaultState().with(Properties.AGE_7, 7), Items.POTATO),
                new HarvestCrop(Blocks.BEETROOTS.getDefaultState().with(Properties.AGE_3, 3), Items.BEETROOT_SEEDS)
        );
    }
}
