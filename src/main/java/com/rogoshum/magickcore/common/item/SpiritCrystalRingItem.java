package com.rogoshum.magickcore.common.item;

import com.rogoshum.magickcore.api.mana.IManaContextItem;
import com.rogoshum.magickcore.common.event.AdvancementsEvent;
import com.rogoshum.magickcore.common.extradata.entity.EntityStateData;
import com.rogoshum.magickcore.common.integration.curios.CuriosHelper;
import com.rogoshum.magickcore.common.lib.LibAdvancements;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;


public class SpiritCrystalRingItem extends ManaItem implements IManaContextItem {
    public SpiritCrystalRingItem(Properties properties) {
        super(properties);
    }

    @Override
    public boolean releaseMagick(LivingEntity playerIn, EntityStateData state, ItemStack stack) {
        return false;
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return false;
    }


    public boolean canEquip(String identifier, LivingEntity livingEntity, ItemStack stack) {
        return !CuriosHelper.hasSpiritRing(livingEntity);
    }

    @Override
    public void inventoryTick(ItemStack stack, Level worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        super.inventoryTick(stack, worldIn, entityIn, itemSlot, isSelected);
    }


    public boolean canRightClickEquip(ItemStack stack) {
        return true;
    }
}
