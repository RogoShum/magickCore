package com.rogoshum.magickcore.api.item;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public interface IDimensionTooltip {
    List<Component> dimensionToolTip(ItemStack stack);
}
