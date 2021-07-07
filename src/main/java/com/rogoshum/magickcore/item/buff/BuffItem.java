package com.rogoshum.magickcore.item.buff;

import com.rogoshum.magickcore.capability.IEntityState;
import com.rogoshum.magickcore.item.ManaItem;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.*;

public class BuffItem extends ManaItem {

    @Override
    public boolean releaseMagick(LivingEntity playerIn, IEntityState state, ItemStack stack) {
        return false;
    }
}
