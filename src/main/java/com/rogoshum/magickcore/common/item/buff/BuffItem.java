package com.rogoshum.magickcore.common.item.buff;

import com.rogoshum.magickcore.common.extradata.entity.EntityStateData;
import com.rogoshum.magickcore.common.item.ManaItem;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.*;

public class BuffItem extends ManaItem {

    public BuffItem() {
        super(properties());
    }

    @Override
    public boolean releaseMagick(LivingEntity playerIn, EntityStateData state, ItemStack stack) {
        return false;
    }
}
