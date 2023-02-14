package com.rogoshum.magickcore.common.recipe;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.api.mana.IMaterialLimit;
import com.rogoshum.magickcore.api.mana.ISpellContext;
import com.rogoshum.magickcore.common.entity.pointed.ContextCreatorEntity;
import com.rogoshum.magickcore.common.init.ManaMaterials;
import com.rogoshum.magickcore.common.init.ModEntities;
import com.rogoshum.magickcore.common.item.ContextCoreItem;
import com.rogoshum.magickcore.common.item.material.ManaMaterialItem;
import com.rogoshum.magickcore.common.lib.LibMaterial;
import com.rogoshum.magickcore.common.magick.context.SpellContext;
import com.rogoshum.magickcore.common.magick.materials.Material;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.SimpleRecipeSerializer;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

import javax.annotation.Nonnull;

public class ManaItemMaterialRecipe extends CustomRecipe {
    private final RecipeSerializer<?> SERIALIZER;

    public ManaItemMaterialRecipe(ResourceLocation idIn) {
        super(new ResourceLocation(MagickCore.MOD_ID, idIn.getPath()));
        SERIALIZER = new SimpleRecipeSerializer<>(res -> this);
        SERIALIZER.setRegistryName(new ResourceLocation(MagickCore.MOD_ID, idIn.getPath()));
    }

    @Override
    public boolean matches(CraftingContainer inv, Level worldIn) {
        ItemStack tool = null;
        ItemStack contextCore = null;
        for(int j = 0; j < inv.getContainerSize(); ++j) {
            ItemStack itemstack = inv.getItem(j);
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
        TempMaterial tempMaterial = new TempMaterial();
        ((ManaMaterialItem)tool.getItem()).upgradeManaItem(tool, tempMaterial);
        String preMaterial = LibMaterial.ORIGIN;
        if(contextCore.hasTag() && contextCore.getTag().contains("mana_material"))
            preMaterial = contextCore.getTag().getString("mana_material");
        return !preMaterial.equals(tempMaterial.getMaterial().getName());
    }

    @Nonnull
    @Override
    public ItemStack assemble(CraftingContainer inv) {
        ItemStack tool = null;
        ItemStack contextCore = null;
        for(int j = 0; j < inv.getContainerSize(); ++j) {
            ItemStack itemstack = inv.getItem(j);
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
        TempMaterial tempMaterial = new TempMaterial();
        ((ManaMaterialItem)tool.getItem()).upgradeManaItem(tool, tempMaterial);
        ItemStack result = contextCore.copy();
        result.getOrCreateTag().putString("mana_material", tempMaterial.material.getName());
        return result;
    }

    public static class TempMaterial implements ISpellContext, IMaterialLimit {
        Material material = ManaMaterials.getMaterial(LibMaterial.ORIGIN);
        @Override
        public Material getMaterial() {
            return material;
        }

        @Override
        public void setMaterial(Material material) {
            this.material = material;
        }

        @Override
        public SpellContext spellContext() {
            return SpellContext.create();
        }
    }

    @Override
    public NonNullList<ItemStack> getRemainingItems(CraftingContainer inv) {
        NonNullList<ItemStack> nonnulllist = NonNullList.withSize(inv.getContainerSize(), ItemStack.EMPTY);

        for(int i = 0; i < nonnulllist.size(); ++i) {
            ItemStack item = inv.getItem(i);
            if(item.getItem() instanceof ManaMaterialItem) {
                nonnulllist.set(i, item);
                inv.setItem(i, ItemStack.EMPTY);
            }
        }

        return nonnulllist;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height)  {
        return width * height >= 2;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return SERIALIZER;
    }

    @Override
    public RecipeType<?> getType() {
        return RecipeType.CRAFTING;
    }
}
