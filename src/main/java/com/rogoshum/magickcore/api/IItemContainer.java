package com.rogoshum.magickcore.api;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;

public interface IItemContainer {
    public Item getItem();

    public boolean hasKey();

    public String[] getKeys();

    public boolean matches(ItemStack stack);
}
