package com.rogoshum.magickcore.common.event.magickevent;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.common.api.enums.ApplyType;
import com.rogoshum.magickcore.common.api.event.ElementEvent;
import com.rogoshum.magickcore.common.event.AdvancementsEvent;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ElementThingEvent {

    @SubscribeEvent
    public void applyFunction(ElementEvent.ElementFunctionApply event) {
        if(event.getMagickContext().victim instanceof ItemEntity && event.getMagickContext().applyType == ApplyType.ATTACK)
            event.setCanceled(true);
    }
}
