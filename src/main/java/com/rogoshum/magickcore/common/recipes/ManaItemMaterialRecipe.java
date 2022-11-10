package com.rogoshum.magickcore.common.recipes;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.common.entity.pointed.ContextCreatorEntity;
import com.rogoshum.magickcore.common.init.ModEntities;
import com.rogoshum.magickcore.common.init.ModRecipes;
import com.rogoshum.magickcore.common.item.ContextCoreItem;
import com.rogoshum.magickcore.common.item.ManaMaterialItem;
import com.rogoshum.magickcore.common.util.NBTTagHelper;
import net.minecraft.entity.Entity;
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

public class ManaItemMaterialRecipe extends SpecialRecipe {
    private final IRecipeSerializer<?> SERIALIZER;

    public ManaItemMaterialRecipe(ResourceLocation idIn) {
        super(new ResourceLocation(MagickCore.MOD_ID, idIn.getPath()));
        SERIALIZER = new SpecialRecipeSerializer<>(res -> this);
        SERIALIZER.setRegistryName(new ResourceLocation(MagickCore.MOD_ID, idIn.getPath()));
    }

    @Override
    public boolean matches(CraftingInventory inv, World worldIn) {
        ItemStack tool = null;
        ItemStack contextCore = null;
        for(int j = 0; j < inv.getSizeInventory(); ++j) {
            ItemStack itemstack = inv.getStackInSlot(j);
            if(itemstack.getItem() instanceof ManaMaterialItem) {
                if(tool != null)
                    return false;
                else
                    tool = itemstack;
            }

            if(itemstack.getItem() instanceof ContextCoreItem) {
                if(contextCore != null)
                    return false;
                else
                    contextCore = itemstack;
            }
        }

        if(tool == null || contextCore == null) return false;
        ContextCreatorEntity toolMaterial = ModEntities.CONTEXT_CREATOR.get().create(worldIn);
        if(toolMaterial == null) return false;
        ((ManaMaterialItem)tool.getItem()).upgradeManaItem(tool, toolMaterial.getInnerManaData());
        Entity createEntity = NBTTagHelper.createEntityByItem(contextCore, worldIn);
        if(createEntity == null) return true;
        ContextCreatorEntity contextMaterial = (ContextCreatorEntity) createEntity;
        return contextMaterial.getMaterial() != toolMaterial.getMaterial();
    }

    @Nonnull
    @Override
    public ItemStack getCraftingResult(CraftingInventory inv) {
        ItemStack tool = null;
        ItemStack contextCore = null;
        for(int j = 0; j < inv.getSizeInventory(); ++j) {
            ItemStack itemstack = inv.getStackInSlot(j);
            if(itemstack.getItem() instanceof ManaMaterialItem) {
                if(tool != null)
                    return ItemStack.EMPTY;
                else
                    tool = itemstack;
            }

            if(itemstack.getItem() instanceof ContextCoreItem) {
                if(contextCore != null)
                    return ItemStack.EMPTY;
                else
                    contextCore = itemstack;
            }
        }

        if(tool == null || contextCore == null) return ItemStack.EMPTY;
        ContextCreatorEntity createEntity = new ContextCreatorEntity(ModEntities.CONTEXT_CREATOR.get(), null);
        ((ManaMaterialItem)tool.getItem()).upgradeManaItem(tool, createEntity.getInnerManaData());
        ItemStack result = contextCore.copy();
        NBTTagHelper.storeEntityToItem(createEntity, result);
        return result;
    }

    @Override
    public NonNullList<ItemStack> getRemainingItems(CraftingInventory inv) {
        NonNullList<ItemStack> nonnulllist = NonNullList.withSize(inv.getSizeInventory(), ItemStack.EMPTY);

        for(int i = 0; i < nonnulllist.size(); ++i) {
            ItemStack item = inv.getStackInSlot(i);
            if(item.getItem() instanceof ManaMaterialItem) {
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

    @Override
    public IRecipeSerializer<?> getSerializer() {
        return SERIALIZER;
    }

    @Override
    public IRecipeType<?> getType() {
        return IRecipeType.CRAFTING;
    }
}
