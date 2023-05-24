package com.rogoshum.magickcore.api.item;

import com.google.common.collect.ImmutableList;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;

public interface IDimensionItem {
    int slotSize(ItemStack stack);
    boolean shouldAddToSlots(@Nullable Entity interactor, ItemStack stack, ImmutableList<ItemStack> slots);
    void onSetToSlot(Entity interactor, ItemStack original, ItemStack copy);
}
