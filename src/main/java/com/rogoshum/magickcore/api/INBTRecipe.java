package com.rogoshum.magickcore.api;

import com.rogoshum.magickcore.recipes.NBTRecipeContainer;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;

import java.util.List;

public interface INBTRecipe {

    public List<NBTRecipeContainer.ItemContainer> getContainers();

    public abstract ItemStack getResultStack(CraftingInventory inv);

    public boolean matches(CraftingInventory inv);

    public INBTRecipe shapeless();
}