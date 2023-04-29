package com.rogoshum.magickcore.common.item;

import com.google.common.collect.ImmutableList;
import com.rogoshum.magickcore.api.itemstack.IDimensionItem;
import com.rogoshum.magickcore.common.item.material.ElementItem;
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
    public boolean shouldAddToSlots(ItemStack stack, ImmutableList<ItemStack> slots) {
        return stack.getItem() instanceof ElementItem;
    }

    @Override
    public void onSetToSlot(ItemStack original, ItemStack copy) {
        original.setCount(0);
    }
}
