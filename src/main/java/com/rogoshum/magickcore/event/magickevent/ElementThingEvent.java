package com.rogoshum.magickcore.event.magickevent;

import com.rogoshum.magickcore.api.event.ElementEvent;
import com.rogoshum.magickcore.enums.EnumApplyType;
import com.rogoshum.magickcore.magick.MagickReleaseHelper;
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
