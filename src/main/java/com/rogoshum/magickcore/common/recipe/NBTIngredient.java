package com.rogoshum.magickcore.common.recipe;

import com.google.common.collect.ImmutableList;
import com.google.gson.*;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.rogoshum.magickcore.common.util.NBTTagHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.TagParser;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.GsonHelper;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.IIngredientSerializer;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Stream;

public class NBTIngredient extends Ingredient {
    private final HashSet<String> keySet;
    private final HashMap<String, Tag> keyMap;
    private final CompoundTag tag;
    private final int count;
    private final String name;
    private final String item;
    private final String itemTag;
    protected NBTIngredient(String item, Ingredient.Value stackList, CompoundTag tag, int count) {
        super(Stream.of(stackList));
        this.tag = tag;
        this.keySet = NBTTagHelper.getNBTKeySet(tag);
        this.keyMap = NBTTagHelper.getNBTKeyMap(tag);
        this.count = count;
        this.item = item;
        this.name = null;
        this.itemTag = null;
    }

    protected NBTIngredient(String name, List<ItemStack> stacks, CompoundTag tag, int count) {
        super(Arrays.stream(stacks.toArray(new ItemStack[0])).filter((stack) -> !stack.isEmpty()).map(Ingredient.ItemValue::new));
        this.tag = tag;
        this.keySet = NBTTagHelper.getNBTKeySet(tag);
        this.keyMap = NBTTagHelper.getNBTKeyMap(tag);
        this.count = count;
        this.item = null;
        this.name = name;
        this.itemTag = null;
    }

    protected NBTIngredient(String itemTag, Stream<? extends Ingredient.Value> itemLists, CompoundTag tag, int count) {
        super(itemLists);
        this.tag = tag;
        this.keySet = NBTTagHelper.getNBTKeySet(tag);
        this.keyMap = NBTTagHelper.getNBTKeyMap(tag);
        this.count = count;
        this.item = null;
        this.name = null;
        this.itemTag = itemTag;
    }

    protected NBTIngredient(int type, String name, Stream<? extends Ingredient.Value> itemLists, CompoundTag tag, int count) {
        super(itemLists);
        this.tag = tag;
        this.keySet = NBTTagHelper.getNBTKeySet(tag);
        this.keyMap = NBTTagHelper.getNBTKeyMap(tag);
        this.count = count;
        if (type == 0) {
            this.item = name;
            this.name = null;
            this.itemTag = null;
        } else if (type == 1) {
            this.item = null;
            this.name = name;
            this.itemTag = null;
        } else {
            this.item = null;
            this.name = null;
            this.itemTag = name;
        }
    }

    public static HashSet<String> getStackNBTKeySet(ItemStack stack) {
        HashSet<String> keys = new HashSet<>();
        if(stack.hasTag()) {
            CompoundTag tag = stack.getTag();
            keys = NBTTagHelper.getNBTKeySet(tag);
        }
        return keys;
    }

    public static HashMap<String, Tag> getStackNBTKeyMap(ItemStack stack) {
        HashMap<String, Tag> keys = new HashMap<>();
        if(stack.hasTag()) {
            CompoundTag tag = stack.getTag();
            keys = NBTTagHelper.getNBTKeyMap(tag);
        }
        return keys;
    }

    @Override
    public boolean test(@Nullable ItemStack input) {
        if(!super.test(input))
            return false;
        HashSet<String> inputKeys = getStackNBTKeySet(input);
        HashMap<String, Tag> inputKeyMap = getStackNBTKeyMap(input);
        if(!inputKeys.containsAll(this.keySet))
            return false;
        for(String key : this.keyMap.keySet()) {
            if(!inputKeyMap.containsKey(key) || !inputKeyMap.get(key).getAsString().equals(this.keyMap.get(key).getAsString()))
                return false;
        }
        return true;
    }

    @Override
    public boolean isSimple()
    {
        return false;
    }

    @Override
    public IIngredientSerializer<? extends Ingredient> getSerializer() {
        return Serializer.INSTANCE;
    }

    @Override
    public JsonElement toJson() {
        JsonObject json = new JsonObject();
        json.addProperty("type", CraftingHelper.getID(Serializer.INSTANCE).toString());
        if(item != null)
            json.addProperty("item", this.item);
        else if(itemTag != null)
            json.addProperty("tag", this.itemTag);
        else if(name != null)
            json.addProperty("name", this.name);
        json.addProperty("count", this.count);
        if (tag != null)
            json.addProperty("nbt", this.tag.getAsString());
        super.toJson();
        return json;
    }

    public static class Serializer implements IIngredientSerializer<NBTIngredient> {
        public static final NBTIngredient.Serializer INSTANCE = new NBTIngredient.Serializer();
        private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

        @Override
        public NBTIngredient parse(FriendlyByteBuf buffer) {
            int limit = buffer.readVarInt();
            Ingredient.Value[] ingredientList = new Ingredient.Value[limit];
            for (int i = 0; i < limit; ++i) {
                ingredientList[i] = new Ingredient.ItemValue(buffer.readItem());
            }

            Stream<? extends Ingredient.Value> itemLists = Stream.of(ingredientList);
            CompoundTag tag = buffer.readNbt();
            int count = buffer.readVarInt();
            int type = buffer.readVarInt();
            String string = buffer.readUtf();
            return new NBTIngredient(type, string, itemLists, tag, count);
        }

        @Override
        public NBTIngredient parse(JsonObject json) {
            if(!json.has("name")) {
                try {
                    int count = GsonHelper.getAsInt(json, "count", 1);
                    CompoundTag nbt = new CompoundTag();
                    if(json.has("nbt")) {
                        JsonElement element = json.get("nbt");
                        if (element.isJsonObject())
                            nbt = TagParser.parseTag(GSON.toJson(element));
                        else
                            nbt = TagParser.parseTag(GsonHelper.convertToString(element, "nbt"));
                    }
                    Stream<? extends Ingredient.Value> itemLists = Stream.of(Ingredient.valueFromJson(json));
                    Optional<? extends Ingredient.Value> optional = itemLists.findFirst();
                    if(json.has("tag")) {
                        String tag = GsonHelper.getAsString(json, "tag");
                        return new NBTIngredient(tag, itemLists, nbt, count);
                    } else {
                        String item = GsonHelper.getAsString(json, "item");
                        return new NBTIngredient(item, optional.get(), nbt, count);
                    }
                } catch (CommandSyntaxException e) {
                    throw new JsonSyntaxException("Invalid NBT Entry: " + e.toString());
                }
            } else {
                try {
                    String name = GsonHelper.getAsString(json, "name");
                    int count = GsonHelper.getAsInt(json, "count", 1);

                    CompoundTag nbt = new CompoundTag();
                    if(json.has("nbt")) {
                        JsonElement element = json.get("nbt");
                        if (element.isJsonObject())
                            nbt = TagParser.parseTag(GSON.toJson(element));
                        else
                            nbt = TagParser.parseTag(GsonHelper.convertToString(element, "nbt"));
                    }

                    ImmutableList.Builder<ItemStack> items = ImmutableList.builder();
                    ForgeRegistries.ITEMS.getKeys().forEach(res -> {
                        if(res.toString().contains(name)) {
                            items.add(new ItemStack(ForgeRegistries.ITEMS.getValue(res)));
                        }
                    });
                    return new NBTIngredient(name, items.build(), nbt, count);
                } catch (CommandSyntaxException e) {
                    throw new JsonSyntaxException("Invalid NBT Entry: " + e.toString());
                }
            }
        }

        @Override
        public void write(FriendlyByteBuf buffer, NBTIngredient ingredient) {
            ItemStack[] items = ingredient.getItems();
            buffer.writeVarInt(items.length);

            for (ItemStack stack : items)
                buffer.writeItem(stack);
            buffer.writeNbt(ingredient.tag);
            buffer.writeVarInt(ingredient.count);
            if(ingredient.item != null) {
                buffer.writeVarInt(0);
                buffer.writeUtf(ingredient.item);
            } else if(ingredient.name != null) {
                buffer.writeVarInt(1);
                buffer.writeUtf(ingredient.name);
            } else {
                buffer.writeVarInt(2);
                buffer.writeUtf(ingredient.itemTag);
            }
        }
    }
}
