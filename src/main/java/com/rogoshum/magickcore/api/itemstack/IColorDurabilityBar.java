package com.rogoshum.magickcore.api.itemstack;

import net.minecraft.world.item.ItemStack;

public interface IColorDurabilityBar {

    boolean showDurabilityBar(ItemStack stack);

    int getRGBDurabilityForDisplay(ItemStack stack);

    double getDurabilityForDisplay(ItemStack stack);
}
