package com.rogoshum.magickcore.common.recipe;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.api.mana.IManaContextItem;
import com.rogoshum.magickcore.common.item.ContextCoreItem;
import com.rogoshum.magickcore.common.item.MagickContextItem;
import com.rogoshum.magickcore.common.extradata.item.ItemManaData;
import com.rogoshum.magickcore.common.init.ModItems;
import com.rogoshum.magickcore.common.extradata.ExtraDataUtil;
import com.rogoshum.magickcore.common.util.NBTTagHelper;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.SimpleRecipeSerializer;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;

import javax.annotation.Nonnull;

public class ManaItemContextRecipe extends CustomRecipe {
    private final RecipeSerializer<?> SERIALIZER;
    public ManaItemContextRecipe(ResourceLocation idIn) {
        super(new ResourceLocation(MagickCore.MOD_ID, idIn.getPath()));
        SERIALIZER = new SimpleRecipeSerializer<>(res -> this);
        SERIALIZER.setRegistryName(new ResourceLocation(MagickCore.MOD_ID, idIn.getPath()));
    }

    @Override
    public boolean matches(CraftingContainer inv, Level worldIn) {
        ItemStack tool = null;
        ItemStack magickContext = null;
        for(int j = 0; j < inv.getContainerSize(); ++j) {
            ItemStack itemstack = inv.getItem(j);
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
    public ItemStack assemble(CraftingContainer inv) {
        ItemStack tool = null;
        ItemStack magickContext = null;
        for(int j = 0; j < inv.getContainerSize(); ++j) {
            ItemStack itemstack = inv.getItem(j);
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
                ItemStack newTool = tool.copy();
                ItemStack newContext = new ItemStack(ModItems.MAGICK_CORE.get());
                ItemManaData manaData = ExtraDataUtil.itemManaData(newContext);
                manaData.spellContext().copy(ExtraDataUtil.itemManaData(tool).spellContext());
                NBTTagHelper.coreItemFromContext(newTool, newContext);
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
                NBTTagHelper.contextItemWithCore(newTool, magickContext);
                return newTool;
            }
        }
    }

    @Override
    public NonNullList<ItemStack> getRemainingItems(CraftingContainer inv) {
        NonNullList<ItemStack> nonnulllist = NonNullList.withSize(inv.getContainerSize(), ItemStack.EMPTY);

        for(int i = 0; i < nonnulllist.size(); ++i) {
            ItemStack item = inv.getItem(i);
            if(item.getItem() instanceof IManaContextItem) {
                ItemManaData manaData = ExtraDataUtil.itemManaData(item);
                if(manaData.contextCore().haveMagickContext()) {
                    manaData.contextCore().setHave(false);
                    manaData.spellContext().clear();
                    if(item.hasTag()) {
                        if(item.hasCustomHoverName())
                            item.setHoverName(null);
                        if(item.getTag().contains("manaItemName")) {
                            Component itextcomponent = Component.Serializer.fromJson(item.getTag().getString("manaItemName"));
                            if (itextcomponent != null) {
                                item.setHoverName(itextcomponent);
                                item.getTag().remove("manaItemName");
                            }
                        }
                    }
                    nonnulllist.set(i, item);
                    inv.setItem(i, ItemStack.EMPTY);
                }
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
