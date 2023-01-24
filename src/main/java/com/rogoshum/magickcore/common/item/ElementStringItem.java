package com.rogoshum.magickcore.common.item;

import com.rogoshum.magickcore.common.event.magickevent.AdvancementsEvent;
import com.rogoshum.magickcore.common.lib.LibAdvancements;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class ElementStringItem extends ElementContainerItem{
    public ElementStringItem() {
        super(BaseItem.properties());
    }

    @Override
    public void inventoryTick(ItemStack p_77663_1_, Level p_77663_2_, Entity p_77663_3_, int p_77663_4_, boolean p_77663_5_) {
        super.inventoryTick(p_77663_1_, p_77663_2_, p_77663_3_, p_77663_4_, p_77663_5_);
        if(p_77663_3_ instanceof ServerPlayer) {
            AdvancementsEvent.STRING_TRIGGER.trigger((ServerPlayer) p_77663_3_, LibAdvancements.ELEMENT_STRING);
        }
    }
}
