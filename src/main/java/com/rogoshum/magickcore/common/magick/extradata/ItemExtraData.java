package com.rogoshum.magickcore.common.magick.extradata;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;

public abstract class ItemExtraData {
    public static final String ITEM_DATA = "item_extra_data";
    public abstract boolean isItemSuitable(ItemStack item);
    public abstract void read(CompoundNBT nbt);
    public abstract void write(CompoundNBT nbt);
    public abstract void fixData(ItemStack stack);
}
