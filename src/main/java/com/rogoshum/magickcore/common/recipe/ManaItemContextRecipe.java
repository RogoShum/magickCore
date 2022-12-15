package com.rogoshum.magickcore.common.recipe;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.api.mana.IManaContextItem;
import com.rogoshum.magickcore.common.item.ContextCoreItem;
import com.rogoshum.magickcore.common.item.MagickContextItem;
import com.rogoshum.magickcore.common.extradata.item.ItemManaData;
import com.rogoshum.magickcore.common.init.ModItems;
import com.rogoshum.magickcore.common.extradata.ExtraDataUtil;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.SpecialRecipe;
import net.minecraft.item.crafting.SpecialRecipeSerializer;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public class ManaItemContextRecipe extends SpecialRecipe {
    private final IRecipeSerializer<?> SERIALIZER;
    public ManaItemContextRecipe(ResourceLocation idIn) {
        super(new ResourceLocation(MagickCore.MOD_ID, idIn.getPath()));
        SERIALIZER = new SpecialRecipeSerializer<>(res -> this);
        SERIALIZER.setRegistryName(new ResourceLocation(MagickCore.MOD_ID, idIn.getPath()));
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
        ItemManaData.ContextCore contextCore = ExtraDataUtil.itemManaData(tool).contextCore();

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
        ItemManaData.ContextCore contextCore = ExtraDataUtil.itemManaData(tool).contextCore();
        if(contextCore.isDisable()) return ItemStack.EMPTY;
        if(contextCore.haveMagickContext()) {
            if(magickContext != null)
                return ItemStack.EMPTY;
            else {
                ItemStack newContext = new ItemStack(ModItems.MAGICK_CORE.get());
                ItemManaData manaData = ExtraDataUtil.itemManaData(newContext);
                manaData.spellContext().copy(ExtraDataUtil.itemManaData(tool).spellContext());
                return newContext;
            }
        } else {
            if(magickContext == null)
                return ItemStack.EMPTY;
            else {
                ItemStack newTool = tool.copy();
                ItemManaData manaData = ExtraDataUtil.itemManaData(newTool);
                manaData.spellContext().copy(ExtraDataUtil.itemManaData(magickContext).spellContext());
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
            if(item.getItem() instanceof IManaContextItem) {
                ItemManaData manaData = ExtraDataUtil.itemManaData(item);
                if(manaData.contextCore().haveMagickContext()) {
                    manaData.contextCore().setHave(false);
                    manaData.spellContext().clear();
                    nonnulllist.set(i, item);
                    inv.setInventorySlotContents(i, ItemStack.EMPTY);
                }
            }
        }

        return nonnulllist;
    }

    @Override
    public boolean canFit(int width, int height)  {
        return width * height >= 2;
    }

    @Override
    public IRecipeSerializer<?> getSerializer() {
        return SERIALIZER;
    }
    @Override
    public IRecipeType<?> getType() {
        return IRecipeType.CRAFTING;
    }
}
