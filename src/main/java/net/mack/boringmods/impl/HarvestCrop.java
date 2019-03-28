package net.mack.boringmods.impl;

import com.google.gson.*;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.property.Property;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.function.Predicate;

public class HarvestCrop implements Predicate<BlockState> {
    private final BlockState mature;
    private Block block;

    public Block getBlock(){
        if (null == this.block)
            this.block = this.mature.getBlock();
        return this.block;
    }

    public HarvestCrop(BlockState state) {
        this.mature = state;
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
            Block block = Registry.BLOCK.get(new Identifier(json.getAsJsonPrimitive("block").getAsString()));
            BlockState state = block.getDefaultState();
            JsonObject stateObject = json.getAsJsonObject("states");
            for (Map.Entry<String, JsonElement> e : stateObject.entrySet()) {
                Property property = block.getStateFactory().getProperty(e.getKey());
                if (property != null) {
                    String valueString = e.getValue().getAsString();
                    Comparable value = (Comparable) property.getValue(valueString).get();
                    state = state.with(property, value);
                }
            }
            return new HarvestCrop(state);
        }

        @Override
        public JsonElement serialize(HarvestCrop src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject object = new JsonObject();
            object.addProperty("block", Registry.BLOCK.getId(src.getBlock()).toString());

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