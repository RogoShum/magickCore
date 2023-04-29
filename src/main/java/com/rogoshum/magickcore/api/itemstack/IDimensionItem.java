package com.rogoshum.magickcore.api.itemstack;

import com.google.common.collect.ImmutableList;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public interface IDimensionItem {
    int slotSize(ItemStack stack);
    boolean shouldAddToSlots(ItemStack stack, ImmutableList<ItemStack> slots);
    void onSetToSlot(ItemStack original, ItemStack copy);
}
