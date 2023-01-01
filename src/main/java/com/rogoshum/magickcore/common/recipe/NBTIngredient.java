package com.rogoshum.magickcore.common.recipe;

import com.google.common.collect.ImmutableList;
import com.google.gson.*;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.rogoshum.magickcore.common.util.NBTTagHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.IIngredientSerializer;
import net.minecraftforge.common.crafting.StackList;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class NBTIngredient extends Ingredient {
    private final HashSet<String> keySet;
    private final HashMap<String, INBT> keyMap;
    private final CompoundNBT tag;
    private final int count;
    private final String name;
    private final String item;
    private final String itemTag;
    protected NBTIngredient(String item, Ingredient.IItemList stackList, CompoundNBT tag, int count) {
        super(Stream.of(stackList));
        this.tag = tag;
        this.keySet = NBTTagHelper.getNBTKeySet(tag);
        this.keyMap = NBTTagHelper.getNBTKeyMap(tag);
        this.count = count;
        this.item = item;
        this.name = null;
        this.itemTag = null;
    }

    protected NBTIngredient(String name, List<ItemStack> stacks, CompoundNBT tag, int count) {
        super(Stream.of(new StackList(stacks)));
        this.tag = tag;
        this.keySet = NBTTagHelper.getNBTKeySet(tag);
        this.keyMap = NBTTagHelper.getNBTKeyMap(tag);
        this.count = count;
        this.item = null;
        this.name = name;
        this.itemTag = null;
    }

    protected NBTIngredient(String itemTag, Stream<? extends Ingredient.IItemList> itemLists, CompoundNBT tag, int count) {
        super(itemLists);
        this.tag = tag;
        this.keySet = NBTTagHelper.getNBTKeySet(tag);
        this.keyMap = NBTTagHelper.getNBTKeyMap(tag);
        this.count = count;
        this.item = null;
        this.name = null;
        this.itemTag = itemTag;
    }

    protected NBTIngredient(int type, String name, Stream<? extends Ingredient.IItemList> itemLists, CompoundNBT tag, int count) {
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
            CompoundNBT tag = stack.getTag();
            keys = NBTTagHelper.getNBTKeySet(tag);
        }
        return keys;
    }

    public static HashMap<String, INBT> getStackNBTKeyMap(ItemStack stack) {
        HashMap<String, INBT> keys = new HashMap<>();
        if(stack.hasTag()) {
            CompoundNBT tag = stack.getTag();
            keys = NBTTagHelper.getNBTKeyMap(tag);
        }
        return keys;
    }

    @Override
    public boolean test(@Nullable ItemStack input) {
        if(!super.test(input))
            return false;
        HashSet<String> inputKeys = getStackNBTKeySet(input);
        HashMap<String, INBT> inputKeyMap = getStackNBTKeyMap(input);
        if(!inputKeys.containsAll(this.keySet))
            return false;
        for(String key : this.keyMap.keySet()) {
            if(!inputKeyMap.containsKey(key) || !inputKeyMap.get(key).getString().equals(this.keyMap.get(key).getString()))
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
    public JsonElement serialize() {
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
            json.addProperty("nbt", this.tag.getString());
        super.serialize();
        return json;
    }

    public static class Serializer implements IIngredientSerializer<NBTIngredient> {
        public static final NBTIngredient.Serializer INSTANCE = new NBTIngredient.Serializer();
        private static Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

        @Override
        public NBTIngredient parse(PacketBuffer buffer) {
            int limit = buffer.readVarInt();
            Ingredient.IItemList[] ingredientList = new Ingredient.IItemList[limit];
            for (int i = 0; i < limit; ++i) {
                ingredientList[i] = new Ingredient.SingleItemList(buffer.readItemStack());
            }

            Stream<? extends Ingredient.IItemList> itemLists = Stream.of(ingredientList);
            CompoundNBT tag = buffer.readCompoundTag();
            int count = buffer.readVarInt();
            int type = buffer.readVarInt();
            String string = buffer.readString();
            return new NBTIngredient(type, string, itemLists, tag, count);
        }

        @Override
        public NBTIngredient parse(JsonObject json) {
            if(!json.has("name")) {
                try {
                    int count = JSONUtils.getInt(json, "count", 1);
                    CompoundNBT nbt = new CompoundNBT();
                    if(json.has("nbt")) {
                        JsonElement element = json.get("nbt");
                        if (element.isJsonObject())
                            nbt = JsonToNBT.getTagFromJson(GSON.toJson(element));
                        else
                            nbt = JsonToNBT.getTagFromJson(JSONUtils.getString(element, "nbt"));
                    }
                    Stream<? extends Ingredient.IItemList> itemLists = Stream.of(Ingredient.deserializeItemList(json));
                    Optional<? extends Ingredient.IItemList> optional = itemLists.findFirst();
                    if(json.has("tag")) {
                        String tag = JSONUtils.getString(json, "tag");
                        return new NBTIngredient(tag, itemLists, nbt, count);
                    } else {
                        String item = JSONUtils.getString(json, "item");
                        return new NBTIngredient(item, optional.get(), nbt, count);
                    }
                } catch (CommandSyntaxException e) {
                    throw new JsonSyntaxException("Invalid NBT Entry: " + e.toString());
                }
            } else {
                try {
                    String name = JSONUtils.getString(json, "name");
                    int count = JSONUtils.getInt(json, "count", 1);

                    CompoundNBT nbt = new CompoundNBT();
                    if(json.has("nbt")) {
                        JsonElement element = json.get("nbt");
                        if (element.isJsonObject())
                            nbt = JsonToNBT.getTagFromJson(GSON.toJson(element));
                        else
                            nbt = JsonToNBT.getTagFromJson(JSONUtils.getString(element, "nbt"));
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
        public void write(PacketBuffer buffer, NBTIngredient ingredient) {
            ItemStack[] items = ingredient.getMatchingStacks();
            buffer.writeVarInt(items.length);

            for (ItemStack stack : items)
                buffer.writeItemStack(stack);
            buffer.writeCompoundTag(ingredient.tag);
            buffer.writeVarInt(ingredient.count);
            if(ingredient.item != null) {
                buffer.writeVarInt(0);
                buffer.writeString(ingredient.item);
            } else if(ingredient.name != null) {
                buffer.writeVarInt(1);
                buffer.writeString(ingredient.name);
            } else {
                buffer.writeVarInt(2);
                buffer.writeString(ingredient.itemTag);
            }
        }
    }
}
