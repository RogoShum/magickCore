package com.rogoshum.magickcore.common.recipe;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.api.mana.IManaContextItem;
import com.rogoshum.magickcore.common.extradata.ExtraDataUtil;
import com.rogoshum.magickcore.common.extradata.item.ItemManaData;
import com.rogoshum.magickcore.common.init.ModItems;
import com.rogoshum.magickcore.common.item.MagickContextItem;
import com.rogoshum.magickcore.common.item.material.ConditionItem;
import com.rogoshum.magickcore.common.lib.LibConditions;
import com.rogoshum.magickcore.common.lib.LibContext;
import com.rogoshum.magickcore.common.magick.condition.BlockOnlyCondition;
import com.rogoshum.magickcore.common.magick.condition.Condition;
import com.rogoshum.magickcore.common.magick.context.SpellContext;
import com.rogoshum.magickcore.common.magick.context.child.ConditionContext;
import com.rogoshum.magickcore.common.registry.MagickRegistry;
import net.minecraft.block.Block;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.SpecialRecipe;
import net.minecraft.item.crafting.SpecialRecipeSerializer;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BlockConditionRecipe extends SpecialRecipe {
    private final IRecipeSerializer<?> SERIALIZER;
    public BlockConditionRecipe(ResourceLocation idIn) {
        super(new ResourceLocation(MagickCore.MOD_ID, idIn.getPath()));
        SERIALIZER = new SpecialRecipeSerializer<>(res -> this);
        SERIALIZER.setRegistryName(new ResourceLocation(MagickCore.MOD_ID, idIn.getPath()));
    }

    @Override
    public boolean matches(CraftingInventory inv, World worldIn) {
        boolean hasBlock = false;
        ItemStack condition = null;
        for(int j = 0; j < inv.getSizeInventory(); ++j) {
            ItemStack itemstack = inv.getStackInSlot(j);
            if(itemstack.getItem() == ModItems.CONDITION_BLOCK.get()) {
                if(condition != null)
                    return false;
                else
                    condition = itemstack;
            } else if(itemstack.getItem() instanceof BlockItem) {
                hasBlock = true;
            }
        }

        return condition != null && hasBlock;
    }

    @Nonnull
    @Override
    public ItemStack getCraftingResult(CraftingInventory inv) {
        List<Block> list = new ArrayList<>();
        ItemStack condition = null;
        for(int j = 0; j < inv.getSizeInventory(); ++j) {
            ItemStack itemstack = inv.getStackInSlot(j);
            if(itemstack.getItem() == ModItems.CONDITION_BLOCK.get()) {
                if(condition != null)
                    return ItemStack.EMPTY;
                else
                    condition = itemstack;
            } else if(itemstack.getItem() instanceof BlockItem) {
                list.add(((BlockItem) itemstack.getItem()).getBlock());
            } else if(!itemstack.isEmpty())
                return ItemStack.EMPTY;
        }
        if(condition == null) return ItemStack.EMPTY;
        ItemStack stack = condition.copy();
        SpellContext context = ExtraDataUtil.itemManaData(stack).spellContext();
        if(!context.containChild(LibContext.CONDITION)) {
            context.addChild(ConditionContext.create());
        }
        ConditionContext conditionContext = context.getChild(LibContext.CONDITION);
        if(list.isEmpty()) {
            conditionContext.addCondition(MagickRegistry.getCondition(LibConditions.BLOCK_ONLY));
            return stack;
        }
        if(conditionContext.conditions.stream().anyMatch(condition1 -> condition1 instanceof BlockOnlyCondition)) {
            Optional<Condition<?>> conditionOptional = conditionContext.conditions.stream().filter(condition1 -> condition1 instanceof BlockOnlyCondition).findFirst();
            if(conditionOptional.isPresent()) {
                BlockOnlyCondition blockOnlyCondition = (BlockOnlyCondition) conditionOptional.get();
                for (Block block : list) {
                    blockOnlyCondition.addBlock(block);
                }
                conditionContext.addCondition(blockOnlyCondition);
            }
        } else {
            BlockOnlyCondition blockOnlyCondition = (BlockOnlyCondition) MagickRegistry.getCondition(LibConditions.BLOCK_ONLY);
            for (Block block : list) {
                blockOnlyCondition.addBlock(block);
            }
            conditionContext.addCondition(blockOnlyCondition);
        }
        context.addChild(conditionContext);
        return stack;
    }

    @Override
    public NonNullList<ItemStack> getRemainingItems(CraftingInventory inv) {
        NonNullList<ItemStack> nonnulllist = NonNullList.withSize(inv.getSizeInventory(), ItemStack.EMPTY);

        for(int i = 0; i < nonnulllist.size(); ++i) {
            ItemStack item = inv.getStackInSlot(i);
            if(item.getItem() instanceof BlockItem) {
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
