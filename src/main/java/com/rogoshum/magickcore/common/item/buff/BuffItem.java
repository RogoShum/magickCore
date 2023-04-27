package com.rogoshum.magickcore.common.item.buff;

import com.rogoshum.magickcore.api.extradata.entity.EntityStateData;
import com.rogoshum.magickcore.common.item.ManaItem;
import net.minecraft.world.entity.LivingEntity;

import net.minecraft.world.item.ItemStack;

public class BuffItem extends ManaItem {

    public BuffItem() {
        super(properties());
    }

    @Override
    public boolean releaseMagick(LivingEntity playerIn, EntityStateData state, ItemStack stack) {
        return false;
    }
}
