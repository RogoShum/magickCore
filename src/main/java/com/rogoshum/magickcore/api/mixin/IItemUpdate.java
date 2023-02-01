package com.rogoshum.magickcore.api.mixin;

import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;

public interface IItemUpdate {
    boolean onEntityItemUpdate(ItemStack stack, ItemEntity entity);
}
