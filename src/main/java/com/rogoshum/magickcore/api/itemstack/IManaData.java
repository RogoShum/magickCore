package com.rogoshum.magickcore.api.itemstack;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public interface IManaData {
    default boolean showDurabilityBar(ItemStack item) {
        return false;
    }
}
