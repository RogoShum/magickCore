package com.rogoshum.magickcore.common.item;

import com.rogoshum.magickcore.api.mana.IManaContextItem;
import com.rogoshum.magickcore.common.extradata.entity.EntityStateData;
import com.rogoshum.magickcore.common.integration.curios.CuriosHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

public class SpiritCrystalRingItem extends ManaItem implements ICurioItem, IManaContextItem {
    public SpiritCrystalRingItem(Properties properties) {
        super(properties);
    }

    @Override
    public boolean releaseMagick(LivingEntity playerIn, EntityStateData state, ItemStack stack) {
        return false;
    }

    @Override
    public boolean canEquip(String identifier, LivingEntity livingEntity, ItemStack stack) {
        return !CuriosHelper.hasSpiritRing(livingEntity);
    }
}
