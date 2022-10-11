package com.rogoshum.magickcore.recipes;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.api.IManaContextItem;
import com.rogoshum.magickcore.init.ModItems;
import com.rogoshum.magickcore.init.ModRecipes;
import com.rogoshum.magickcore.item.MagickContextItem;
import com.rogoshum.magickcore.magick.extradata.item.ItemManaData;
import com.rogoshum.magickcore.tool.ExtraDataHelper;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.SpecialRecipe;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public class ManaItemContextRecipe extends SpecialRecipe {
    public ManaItemContextRecipe(ResourceLocation idIn) {
        super(idIn);
    }

    @Override
    public boolean matches(CraftingInventory inv, World worldIn) {
        ItemStack tool = null;
        ItemStack magickContext = null;
        for(int j = 0; j < inv.getSizeInventory(); ++j) {
            ItemStack itemstack = inv.getStackInSlot(j);
            if(itemstack.getItem() instanceof IManaContextItem) {
                if(tool != null)
                    return false;
                else
                    tool = itemstack;
            }

            if(itemstack.getItem() instanceof MagickContextItem) {
                if(magickContext != null)
                    return false;
                else
                    magickContext = itemstack;
            }
        }

        if(tool == null) return false;
        ItemManaData.ContextCore contextCore = ExtraDataHelper.itemManaData(tool).contextCore();

        return !contextCore.isDisable() && (!contextCore.haveMagickContext() && magickContext != null) || (contextCore.haveMagickContext() && magickContext == null);
    }

    @Nonnull
    @Override
    public ItemStack getCraftingResult(CraftingInventory inv) {
        ItemStack tool = null;
        ItemStack magickContext = null;
        for(int j = 0; j < inv.getSizeInventory(); ++j) {
            ItemStack itemstack = inv.getStackInSlot(j);
            if(itemstack.getItem() instanceof IManaContextItem) {
                if(tool != null)
                    return ItemStack.EMPTY;
                else
                    tool = itemstack;
            }

            if(itemstack.getItem() instanceof MagickContextItem) {
                if(magickContext != null)
                    return ItemStack.EMPTY;
                else
                    magickContext = itemstack;
            }
        }

        if(tool == null) return ItemStack.EMPTY;
        ItemManaData.ContextCore contextCore = ExtraDataHelper.itemManaData(tool).contextCore();
        if(contextCore.isDisable()) return ItemStack.EMPTY;
        if(contextCore.haveMagickContext()) {
            if(magickContext != null)
                return ItemStack.EMPTY;
            else {
                ItemStack newContext = new ItemStack(ModItems.MAGICK_CORE.get());
                ItemManaData manaData = ExtraDataHelper.itemManaData(newContext);
                manaData.spellContext().copy(ExtraDataHelper.itemManaData(tool).spellContext());
                return newContext;
            }
        } else {
            if(magickContext == null)
                return ItemStack.EMPTY;
            else {
                ItemStack newTool = tool.copy();
                ItemManaData manaData = ExtraDataHelper.itemManaData(newTool);
                manaData.spellContext().copy(ExtraDataHelper.itemManaData(magickContext).spellContext());
                manaData.contextCore().setHave(true);
                return newTool;
            }
        }
    }

    @Override
    public NonNullList<ItemStack> getRemainingItems(CraftingInventory inv) {
        NonNullList<ItemStack> nonnulllist = NonNullList.withSize(inv.getSizeInventory(), ItemStack.EMPTY);

        for(int i = 0; i < nonnulllist.size(); ++i) {
            ItemStack item = inv.getStackInSlot(i);
            ItemManaData manaData = ExtraDataHelper.itemManaData(item);
            if(item.getItem() instanceof IManaContextItem && manaData.contextCore().haveMagickContext()) {
                manaData.contextCore().setHave(false);
                manaData.spellContext().clear();
                nonnulllist.set(i, item);
                inv.setInventorySlotContents(i, ItemStack.EMPTY);
            }
        }

        return nonnulllist;
    }

    @Override
    public boolean canFit(int width, int height)  {
        return width * height >= 2;
    }

    @Nonnull
    @Override
    public IRecipeSerializer<?> getSerializer() {
        return ModRecipes.context_tool_recipe;
    }
}
