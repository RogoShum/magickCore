package com.rogoshum.magickcore.common.event.magickevent;

import com.rogoshum.magickcore.api.enums.ApplyType;
import com.rogoshum.magickcore.api.event.ElementEvent;
import net.minecraft.entity.item.ItemEntity;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ElementFunctionEvent {

    @SubscribeEvent
    public void applyFunction(ElementEvent.ElementFunctionApply event) {
        if(event.getMagickContext().victim instanceof ItemEntity && event.getMagickContext().applyType == ApplyType.ATTACK)
            event.setCanceled(true);
    }
}
