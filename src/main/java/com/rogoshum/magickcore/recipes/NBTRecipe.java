package com.rogoshum.magickcore.recipes;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.helper.NBTTagHelper;
import com.rogoshum.magickcore.init.ModItems;
import com.rogoshum.magickcore.init.ModRecipes;
import com.rogoshum.magickcore.item.OrbBottleItem;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.SpecialRecipe;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Mod;

public class NBTRecipe extends SpecialRecipe {
    private final NBTRecipeContainer container;

    public NBTRecipe(NBTRecipeContainer container, ResourceLocation idIn) {
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
