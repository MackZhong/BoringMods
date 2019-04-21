package net.mack.boringmods.impl;

import com.google.gson.*;
import com.google.gson.annotations.JsonAdapter;
import javafx.beans.property.StringProperty;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemProvider;
import net.minecraft.state.property.IntegerProperty;
import net.minecraft.state.property.Property;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

@JsonAdapter(HarvestCrop.Adapter.class)
public class HarvestCrop implements Predicate<BlockState> {
    private final BlockState mature;
    private Block crop;
    private Item seed;

    public BlockState getMature(){
        return this.mature;
    }

    public Block getCrop(){
        if (null == this.crop)
            this.crop = this.mature.getBlock();
        return this.crop;
    }

    public Item getSeed(){
        return this.seed;
    }

    public HarvestCrop(BlockState state, ItemProvider seedProvider) {
        this.mature = state;
        this.seed = seedProvider.getItem();
    }

    /**
     * Evaluates this predicate on the given argument.
     *
     * @param blockState the input argument
     * @return {@code true} if the input argument matches the predicate,
     * otherwise {@code false}
     */
    @Override
    public boolean test(BlockState blockState) {
        return blockState == this.mature;
    }

    public static class Adapter implements JsonSerializer<HarvestCrop>, JsonDeserializer<HarvestCrop> {
        @Override
        public HarvestCrop deserialize(JsonElement element, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject json = element.getAsJsonObject();
            Block block = Registry.BLOCK.get(new Identifier(json.getAsJsonPrimitive("crop").getAsString()));
            BlockState state = block.getDefaultState();
            JsonObject stateObject = json.getAsJsonObject("states");
            for (Map.Entry<String, JsonElement> e : stateObject.entrySet()) {
                Property property = block.getStateFactory().getProperty(e.getKey());
                if (property != null) {
                    String valueString = e.getValue().getAsString();
                    Optional op = property.getValue(valueString);
                    if (op.isPresent()) {
                        Comparable value = (Comparable) op.get();
                        state = state.with(property, value);
                    }
                }
            }
            Item seed = Registry.ITEM.get(new Identifier(json.getAsJsonObject("seed").getAsString()));
            return new HarvestCrop(state, seed);
        }

        @Override
        public JsonElement serialize(HarvestCrop src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject object = new JsonObject();
            object.addProperty("crop", Registry.BLOCK.getId(src.getCrop()).toString());
            object.addProperty("seed", Registry.ITEM.getId(src.getSeed()).toString());

            String stateString = src.mature.toString();
            String[] properties = stateString.substring(stateString.indexOf("[") + 1, stateString.length() - 1).split(",");

            JsonObject states = new JsonObject();
            for (String property : properties) {
                String[] split = property.split("=");
                states.addProperty(split[0], split[1]);
            }
            object.add("states", states);

            return object;
        }
    }
}
