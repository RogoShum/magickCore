package com.rogoshum.magickcore.common.recipe;

import com.google.common.collect.Sets;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.common.entity.PlaceableItemEntity;
import com.rogoshum.magickcore.common.util.MultiBlockUtil;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

import java.util.*;

public class SpiritCraftingRecipe implements Recipe<MatrixInventory> {
    public static final RecipeType<SpiritCraftingRecipe> SPIRIT_CRAFTING = Registry.register(Registry.RECIPE_TYPE, MagickCore.MOD_ID, new RecipeType<SpiritCraftingRecipe>() {
        public String toString() {
            return "spirit_crafting";
        }
    });
    private final int recipeY;
    private final int recipeX;
    private final int recipeZ;
    private final NonNullList<Ingredient>[] ingredientList;
    private final ItemStack output;
    private final String group;
    private final ResourceLocation id;

    public SpiritCraftingRecipe(ResourceLocation idIn, String groupIn, int recipeWidthIn, int recipeHeightIn, NonNullList<Ingredient>[] ingredientList, ItemStack output) {
        this.ingredientList = ingredientList;
        this.output = output;
        this.id = idIn;
        this.group = groupIn;
        this.recipeY = this.ingredientList.length;
        this.recipeZ = recipeHeightIn;
        this.recipeX = recipeWidthIn;
    }

    public int getRecipeX() {
        return recipeX;
    }

    public int getRecipeY() {
        return recipeY;
    }

    public int getRecipeZ() {
        return recipeZ;
    }

    public NonNullList<Ingredient>[] getIngredientList() {
        return ingredientList;
    }

    @Override
    public boolean matches(MatrixInventory inv, Level worldIn) {
        Optional<PlaceableItemEntity>[][][] structure = inv.getMatrix();
        if(inv.getY() < 1 || inv.getX() < 1 || inv.getZ() < 1) return false;
        if(inv.getY()!= ingredientList.length) return false;
        if(inv.getX() != recipeX && inv.getX() != recipeZ) return false;
        if(inv.getZ() != recipeZ && inv.getZ() != recipeX) return false;

        int[][] matchedDirection = new int[ingredientList.length][4];
        int[] rightDirection = new int[4];

        for (int y = 0; y < structure.length; ++y) {
            for (int i = 0; i < 4; ++i) {
                Optional<PlaceableItemEntity>[][] rotated = structure[y];
                for (int r = 0; r < i; ++r) {
                    rotated = MultiBlockUtil.rotate(rotated);
                }
                NonNullList<Ingredient> ingredients = ingredientList[y];
                if(MultiBlockUtil.correctStructure(ingredients, rotated)) {
                    matchedDirection[y][i] = 1;
                    rightDirection[i] = 1;
                }
            }
        }

        if(rightDirection[0] == 0 && rightDirection[1] == 0 && rightDirection[2] == 0 && rightDirection[3] == 0) return false;

        for (int i = 0; i < 4; ++i) {
            if(rightDirection[i] == 0) continue;
            boolean levelMatch = true;
            for (int[] array : matchedDirection) {
                if (array[i] == 0) {
                    levelMatch = false;
                    break;
                }
            }
            if (levelMatch)
                return true;
        }

        return false;
    }

    @Override
    public ItemStack assemble(MatrixInventory inv) {
        return getResultItem().copy();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return false;
    }

    @Override
    public ItemStack getResultItem() {
        return output;
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return Serializer.INSTANCE;
    }

    @Override
    public RecipeType<?> getType() {
        return SPIRIT_CRAFTING;
    }

    public static NonNullList<Ingredient> deserializeIngredients(String[] pattern, Map<String, Ingredient> keys, int patternWidth, int patternHeight) {
        NonNullList<Ingredient> nonnulllist = NonNullList.withSize(patternWidth * patternHeight, Ingredient.EMPTY);

        for(int i = 0; i < pattern.length; ++i) {
            for(int j = 0; j < pattern[i].length(); ++j) {
                String s = pattern[i].substring(j, j + 1);
                Ingredient ingredient = keys.get(s);
                if (ingredient == null) {
                    throw new JsonSyntaxException("Pattern references symbol '" + s + "' but it's not defined in the key");
                }

                nonnulllist.set(j + patternWidth * i, ingredient);
            }
        }

        return nonnulllist;
    }

    public static class Serializer implements RecipeSerializer<SpiritCraftingRecipe> {
        private static final ResourceLocation NAME = new ResourceLocation(MagickCore.MOD_ID, "spirit_crafting");
        public static final RecipeSerializer<?> INSTANCE = new SpiritCraftingRecipe.Serializer();
        public SpiritCraftingRecipe fromJson(ResourceLocation recipeId, JsonObject json) {
            String s = GsonHelper.getAsString(json, "group", "");
            Map<String, Ingredient> map = NBTRecipe.deserializeKey(GsonHelper.getAsJsonObject(json, "key"));
            JsonArray patternArray = GsonHelper.getAsJsonArray(json, "pattern");
            List<NonNullList<Ingredient>> ingredientList = new ArrayList<>();
            int x = 0;
            int z = 0;
            for(int k = 0; k < patternArray.size(); ++k) {
                String[] astring = NBTRecipe.patternFromJson((JsonArray) patternArray.get(k));
                int i = astring[0].length();
                int j = astring.length;
                if(x == 0)
                    x = i;
                else if(x != i)
                    throw new JsonSyntaxException("Inconsistent recipe size '" + recipeId + "'");
                if(z == 0)
                    z = j;
                else if(z != j)
                    throw new JsonSyntaxException("Inconsistent recipe size '" + recipeId + "'");
                NonNullList<Ingredient> nonnulllist = deserializeIngredients(astring, map, i, j);
                ingredientList.add(nonnulllist);
            }

            ItemStack itemstack = NBTRecipe.deserializeItem(GsonHelper.getAsJsonObject(json, "result"));
            NonNullList<Ingredient>[] nonNullLists = new NonNullList[ingredientList.size()];
            for (int i = 0; i < ingredientList.size(); ++i) {
                nonNullLists[i] = ingredientList.get(i);
            }
            return new SpiritCraftingRecipe(recipeId, s, x, z, nonNullLists, itemstack);
        }

        public SpiritCraftingRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
            int y = buffer.readVarInt();
            int x = buffer.readVarInt();
            int z = buffer.readVarInt();
            String s = buffer.readUtf(32767);
            NonNullList<Ingredient>[] recipeList = new NonNullList[y];
            for(int i = 0; i < y; ++i) {
                NonNullList<Ingredient> nonnulllist = NonNullList.withSize(x * z, Ingredient.EMPTY);

                for(int k = 0; k < nonnulllist.size(); ++k) {
                    nonnulllist.set(k, Ingredient.fromNetwork(buffer));
                }
                recipeList[i] = nonnulllist;
            }

            return new SpiritCraftingRecipe(recipeId, s, x, z, recipeList, buffer.readItem());
        }

        public void toNetwork(FriendlyByteBuf buffer, SpiritCraftingRecipe recipe) {
            buffer.writeVarInt(recipe.recipeY);
            buffer.writeVarInt(recipe.recipeX);
            buffer.writeVarInt(recipe.recipeZ);
            buffer.writeUtf(recipe.group);

            for(NonNullList<Ingredient> ingredients : recipe.ingredientList) {
                for(Ingredient ingredient : ingredients) {
                    ingredient.toNetwork(buffer);
                }
            }

            buffer.writeItem(recipe.output);
        }
    }
}
