package com.rogoshum.magickcore.api.extradata;

import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;

public abstract class ItemExtraData {
    public static final String ITEM_DATA = "item_extra_data";
    public ItemExtraData(ItemStack stack) {}
    public abstract boolean isItemSuitable(ItemStack item);
    public abstract void read(CompoundTag nbt);
    public abstract void write(CompoundTag nbt);
    public abstract void fixData(ItemStack stack);
}
