package com.rogoshum.magickcore.common.item;

import com.rogoshum.magickcore.common.event.AdvancementsEvent;
import com.rogoshum.magickcore.common.lib.LibAdvancements;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.World;

public class ElementStringItem extends ElementContainerItem{
    public ElementStringItem() {
        super(BaseItem.properties());
    }

    @Override
    public void inventoryTick(ItemStack p_77663_1_, World p_77663_2_, Entity p_77663_3_, int p_77663_4_, boolean p_77663_5_) {
        super.inventoryTick(p_77663_1_, p_77663_2_, p_77663_3_, p_77663_4_, p_77663_5_);
        if(p_77663_3_ instanceof ServerPlayerEntity) {
            AdvancementsEvent.STRING_TRIGGER.trigger((ServerPlayerEntity) p_77663_3_, LibAdvancements.ELEMENT_STRING);
        }
    }
}
