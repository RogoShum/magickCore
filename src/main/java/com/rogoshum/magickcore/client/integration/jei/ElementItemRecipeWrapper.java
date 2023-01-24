package com.rogoshum.magickcore.client.integration.jei;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.rogoshum.magickcore.api.INBTRecipe;
import com.rogoshum.magickcore.common.init.ModElements;
import com.rogoshum.magickcore.common.init.ModEntities;
import com.rogoshum.magickcore.common.init.ModItems;
import com.rogoshum.magickcore.common.init.ModRecipes;
import com.rogoshum.magickcore.common.lib.LibRegistry;
import com.rogoshum.magickcore.common.recipe.NBTRecipe;
import com.rogoshum.magickcore.common.registry.MagickRegistry;
import com.rogoshum.magickcore.common.util.NBTTagHelper;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.ingredient.IGuiItemStackGroup;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IFocus;
import mezz.jei.api.recipe.category.extensions.vanilla.crafting.ICraftingCategoryExtension;
import mezz.jei.api.recipe.category.extensions.vanilla.crafting.ICustomCraftingCategoryExtension;
import net.minecraft.world.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.crafting.CraftingHelper;


import org.jetbrains.annotations.Nullable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

public class ElementItemRecipeWrapper implements ICustomCraftingCategoryExtension {
    private final ResourceLocation name;
    public ElementItemRecipeWrapper(NBTRecipe recipe) {
        this.name = recipe.getId();
    }

    @Override
    public void setRecipe( IRecipeLayout iRecipeLayout,  IIngredients ingredients) {
        iRecipeLayout.getItemStacks().set(ingredients);
        iRecipeLayout.setShapeless();

        if(!RecipeCollector.RECIPES.containsKey(name)) return;
        List<List<ItemStack>> recipes = RecipeCollector.RECIPES.get(name);
        iRecipeLayout.getItemStacks().init(0, false, 94, 18);

        for(int y = 0; y < 3; ++y) {
            for(int x = 0; x < 3; ++x) {
                int index = 1 + x + y * 3;
                iRecipeLayout.getItemStacks().init(index, true, x * 18, y * 18);
            }
        }
        if(iRecipeLayout.getFocus() != null) {
            ItemStack stack = (ItemStack) iRecipeLayout.getFocus().getValue();
            HashSet<Integer> index = new HashSet<>();
            if(iRecipeLayout.getFocus().getMode() == IFocus.Mode.OUTPUT) {
                for (int i = 0; i < recipes.get(0).size(); ++i) {
                    ItemStack ingredient = recipes.get(0).get(i);
                    if(ingredient.getItem() == stack.getItem() &&
                            ((stack.hasTag() && ingredient.hasTag() && stack.getTag().toString().contains(ingredient.getTag().toString())) || (!stack.hasTag() && !ingredient.hasTag())))
                        index.add(i);
                }
            } else if(iRecipeLayout.getFocus().getMode() == IFocus.Mode.INPUT) {
                for (int i = 1; i < Math.min(recipes.size(), 10); ++i) {
                    for (int c = 0; c < recipes.get(i).size(); ++c) {
                        ItemStack ingredient = recipes.get(i).get(c);
                        if(ingredient.getItem() == stack.getItem() &&
                                ((stack.hasTag() && ingredient.hasTag() && stack.getTag().toString().contains(ingredient.getTag().toString())) || (!stack.hasTag() && !ingredient.hasTag()))) {
                            index.add(c);
                        }
                    }
                }
            }

            for(int i = 0; i < Math.min(recipes.size(), 10); i++) {
                List<ItemStack> items = recipes.get(i);
                List<ItemStack> stacks = new ArrayList<>();
                for (int c : index) {
                    stacks.add(items.get(c));
                }
                iRecipeLayout.getItemStacks().set(i, stacks);
            }
        } else {
            for(int i = 0; i < Math.min(recipes.size(), 10); i++) {
                List<ItemStack> items = recipes.get(i);
                iRecipeLayout.getItemStacks().set(i, items);
            }
        }
    }

    @Override
    public void setIngredients( IIngredients ingredients) {
        if(RecipeCollector.INPUTS.containsKey(name)) {
            ingredients.setInputLists(VanillaTypes.ITEM, RecipeCollector.INPUTS.get(name));
        }
        if(RecipeCollector.OUTPUTS.containsKey(name)) {
            ingredients.setOutputs(VanillaTypes.ITEM, RecipeCollector.OUTPUTS.get(name));
        }
    }

    @Nullable
    @Override
    public ResourceLocation getRegistryName() {
        return name;
    }
}
