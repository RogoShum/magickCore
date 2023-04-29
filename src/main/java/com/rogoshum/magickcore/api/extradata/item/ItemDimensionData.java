package com.rogoshum.magickcore.api.extradata.item;

import com.google.common.collect.ImmutableList;
import com.rogoshum.magickcore.api.extradata.ItemExtraData;
import com.rogoshum.magickcore.api.itemstack.IDimensionItem;
import com.rogoshum.magickcore.api.itemstack.IManaData;
import com.rogoshum.magickcore.common.recipe.ElementToolRecipe;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class ItemDimensionData extends ItemExtraData {
    public static final String DIMENSION_ITEM = "dimension_item";
    private final List<ItemStack> slots;
    public ItemDimensionData(ItemStack stack) {
        super(stack);
        if(isItemSuitable(stack)) {
            slots = NonNullList.withSize(((IDimensionItem)stack.getItem()).slotSize(stack), ItemStack.EMPTY);
            read(stack.getOrCreateTag());
        } else if(ElementToolRecipe.isTool(stack))
            slots = NonNullList.withSize(3, ItemStack.EMPTY);
        else
            slots = ImmutableList.of();
    }

    @Override
    public boolean isItemSuitable(ItemStack item) {
        return item.getItem() instanceof IDimensionItem;
    }

    public void setSlot(int i, ItemStack stack) {
        slots.set(i, stack);
        write(getItem().getOrCreateTag());
    }

    public ImmutableList<ItemStack> getSlots() {
        ImmutableList.Builder<ItemStack> i = new ImmutableList.Builder<>();
        i.addAll(slots);
        return i.build();
    }

    @Override
    public void read(CompoundTag nbt) {
        if(nbt.contains(DIMENSION_ITEM)) {
            try {
                CompoundTag dimension = nbt.getCompound(DIMENSION_ITEM);
                for(String key : dimension.getAllKeys()) {
                    CompoundTag tag = dimension.getCompound(key);
                    slots.add(ItemStack.of(tag));
                }
            } catch (Exception ignored) {}
        }
    }

    @Override
    public void write(CompoundTag nbt) {
        CompoundTag dimension = new CompoundTag();
        for(int i = 0; i < slots.size(); ++i) {
            ItemStack item = slots.get(i);
            CompoundTag tag = item.save(new CompoundTag());
            dimension.put(String.valueOf(i), tag);
        }
        nbt.put(DIMENSION_ITEM, dimension);
    }

    @Override
    public void fixData(ItemStack stack) {

    }
}
