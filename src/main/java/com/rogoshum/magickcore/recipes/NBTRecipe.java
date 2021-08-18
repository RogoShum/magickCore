package com.rogoshum.magickcore.recipes;

import com.rogoshum.magickcore.api.INBTRecipe;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.SpecialRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class NBTRecipe extends SpecialRecipe {
    private final INBTRecipe container;

    public NBTRecipe(INBTRecipe container, ResourceLocation idIn) {
        super(idIn);
        this.container = container;
    }

    @Override
    public boolean matches(CraftingInventory inv, World worldIn) {
        return container.matches(inv);
    }

    @Override
    public ItemStack getCraftingResult(CraftingInventory inv) {
        return container.getResultStack(inv);
    }

    @Override
    public boolean canFit(int width, int height) {
        return width * height >= 2;
    }

    @Override
    public IRecipeSerializer<?> getSerializer() {
        return null;
    }
}
