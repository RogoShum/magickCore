package com.rogoshum.magickcore.common.item;

import com.google.common.collect.ImmutableList;
import com.rogoshum.magickcore.api.item.IDimensionItem;
import com.rogoshum.magickcore.common.item.material.ElementItem;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;

public class CrystalBoxItem extends BaseItem implements IDimensionItem {
    public CrystalBoxItem() {
        super(properties());
    }

    @Override
    public int slotSize(ItemStack stack) {
        return 4;
    }

    @Override
    public boolean shouldAddToSlots(Entity interactor, ItemStack stack, ImmutableList<ItemStack> slots) {
        return stack.getItem() instanceof ElementItem;
    }

    @Override
    public void onSetToSlot(Entity interactor, ItemStack original, ItemStack copy) {
        original.shrink(1);
        copy.setCount(1);
    }
}
