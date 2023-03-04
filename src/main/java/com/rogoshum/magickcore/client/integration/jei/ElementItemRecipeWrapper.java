package com.rogoshum.magickcore.client.integration.jei;

import com.rogoshum.magickcore.common.recipe.NBTRecipe;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.ingredient.ICraftingGridHelper;
import mezz.jei.api.recipe.IFocus;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.category.extensions.vanilla.crafting.ICraftingCategoryExtension;
import net.minecraft.world.item.ItemStack;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.util.Size2i;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class ElementItemRecipeWrapper implements ICraftingCategoryExtension {

    private final ResourceLocation name;
    private final NBTRecipe recipe;
    public ElementItemRecipeWrapper(NBTRecipe recipe) {
        this.name = recipe.getId();
        this.recipe = recipe;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder iRecipeLayout, ICraftingGridHelper craftingGridHelper, IFocusGroup focuses) {
        iRecipeLayout.setShapeless();
        if(!RecipeCollector.RECIPES.containsKey(name)) return;
        List<List<ItemStack>> recipes = RecipeCollector.RECIPES.get(name);
        int width = getWidth();
        int height = getHeight();
        if(!focuses.isEmpty()) {
            IFocus<?> focus = focuses.getAllFocuses().get(0);
            if(focus.getTypedValue().getIngredient() instanceof ItemStack stack) {
                HashSet<Integer> index = new HashSet<>();
                if(focus.getRole() == RecipeIngredientRole.OUTPUT) {
                    for (int i = 0; i < recipes.get(0).size(); ++i) {
                        ItemStack ingredient = recipes.get(0).get(i);
                        if(ingredient.getItem() == stack.getItem() &&
                                ((stack.hasTag() && ingredient.hasTag() && stack.getTag().toString().contains(ingredient.getTag().toString())) || (!stack.hasTag() && !ingredient.hasTag())))
                            index.add(i);
                    }
                } else {
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

                List<List<ItemStack>> newRecipes = new ArrayList<>();
                for(int i = 0; i < recipes.size(); ++i) {
                    List<ItemStack> items = recipes.get(i);
                    List<ItemStack> stacks = new ArrayList<>();
                    for (int c : index) {
                        stacks.add(items.get(c));
                    }
                    newRecipes.add(stacks);
                }
                recipes = newRecipes;
            }
        }

        List<List<ItemStack>> inputs = new ArrayList<>();
        for(int i = 1; i < recipes.size(); ++i) {
            inputs.add(recipes.get(i));
        }
        craftingGridHelper.createAndSetOutputs(iRecipeLayout, VanillaTypes.ITEM_STACK, recipes.get(0));
        craftingGridHelper.createAndSetInputs(iRecipeLayout, VanillaTypes.ITEM_STACK, inputs, width, height);
    }

    @Override
    public int getWidth() {
        return recipe.getWidth();
    }

    @Override
    public int getHeight() {
        return recipe.getHeight();
    }

    @Nullable
    @Override
    public ResourceLocation getRegistryName() {
        return name;
    }
}
