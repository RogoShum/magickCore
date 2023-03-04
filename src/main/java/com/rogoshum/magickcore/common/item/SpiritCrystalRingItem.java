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
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurio;
import top.theillusivec4.curios.api.type.capability.ICurioItem;


public class SpiritCrystalRingItem extends ManaItem implements IManaContextItem, ICurioItem {
    public final ItemStack INSTANCE = new ItemStack(this);
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

    @Override
    public void inventoryTick(ItemStack stack, Level worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        super.inventoryTick(stack, worldIn, entityIn, itemSlot, isSelected);
    }

    @Override
    public boolean canEquip(SlotContext slotContext, ItemStack stack) {
        return !CuriosHelper.hasSpiritRing(slotContext.entity());
    }

    @Override
    public boolean canEquipFromUse(SlotContext slotContext, ItemStack stack) {
        return true;
    }
}
