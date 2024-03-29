package com.rogoshum.magickcore.common.extradata;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

public abstract class ItemExtraData {
    public static final String ITEM_DATA = "item_extra_data";
    public ItemExtraData(ItemStack stack) {}
    public abstract boolean isItemSuitable(ItemStack item);
    public abstract void read(CompoundTag nbt);
    public abstract void write(CompoundTag nbt);
    public abstract void fixData(ItemStack stack);
}
