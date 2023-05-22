package com.rogoshum.magickcore.api.itemstack;

import com.google.common.collect.ImmutableList;
import com.rogoshum.magickcore.api.extradata.ExtraDataUtil;
import com.rogoshum.magickcore.api.extradata.item.ItemManaData;
import com.rogoshum.magickcore.common.item.material.ElementItem;
import com.rogoshum.magickcore.common.item.material.ManaEnergyItem;
import net.minecraft.world.item.ItemStack;

public interface ISpiritDimension extends IDimensionItem {
    @Override
    default int slotSize(ItemStack stack) {
        return 6;
    }

    @Override
    default boolean shouldAddToSlots(ItemStack stack, ImmutableList<ItemStack> slots) {
        if(stack.getItem() instanceof ManaEnergyItem) {
            ItemManaData data = ExtraDataUtil.itemManaData(stack);
            int flag = 0;
            if(data.spellContext().tick() > 0)
                flag++;
            if(data.spellContext().range() > 0)
                flag++;
            if(data.spellContext().force() > 0)
                flag++;
            if(flag != 1) return false;

            for(ItemStack energy : slots) {
                if(energy.getItem() instanceof ManaEnergyItem) {
                    ItemManaData energyData = ExtraDataUtil.itemManaData(stack);
                    if(energyData.spellContext().force() > 0 && data.spellContext().force() > 0)
                        return false;
                    if(energyData.spellContext().range() > 0 && data.spellContext().range() > 0)
                        return false;
                    if(energyData.spellContext().tick() > 0 && data.spellContext().tick() > 0)
                        return false;
                }
            }
            return true;
        }
        return stack.getItem() instanceof ElementItem;
    }

    @Override
    default void onSetToSlot(ItemStack original, ItemStack copy) {
        original.shrink(1);
        copy.setCount(1);
    }
}
