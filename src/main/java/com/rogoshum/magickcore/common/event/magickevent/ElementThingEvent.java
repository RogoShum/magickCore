package com.rogoshum.magickcore.common.event.magickevent;

import com.rogoshum.magickcore.common.api.event.ElementEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ElementThingEvent {

    @SubscribeEvent
    public void applyFunction(ElementEvent.ElementFunctionApply event) {
        /*
        boolean sameLikeOwner = MagickReleaseHelper.sameLikeOwner(event.getMagickContext().caster, event.getMagickContext().victim);

        if(event.getMagickContext().applyType.equals(EnumApplyType.ATTACK) && sameLikeOwner)
            event.setCanceled(true);

        if(event.getMagickContext().applyType.equals(EnumApplyType.DE_BUFF) && sameLikeOwner)
            event.setCanceled(true);

        if(event.getMagickContext().applyType.equals(EnumApplyType.HIT_ENTITY) && sameLikeOwner)
            event.setCanceled(true);

         */
    }
}
