package com.rogoshum.magickcore.item.buff;

import com.rogoshum.magickcore.item.ManaItem;
import com.rogoshum.magickcore.magick.extradata.entity.EntityStateData;
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
