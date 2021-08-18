package com.rogoshum.magickcore.event.magickevent;

import com.rogoshum.magickcore.api.event.ElementEvent;
import com.rogoshum.magickcore.enums.EnumManaType;
import com.rogoshum.magickcore.tool.MagickReleaseHelper;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ElementThingEvent {

    @SubscribeEvent
    public void applyFunction(ElementEvent.ElementFunctionApply event)
    {
        boolean sameLikeOwner = MagickReleaseHelper.sameLikeOwner(event.getReleaseAttribute().entity, event.getReleaseAttribute().victim);

        if(event.getManaType().equals(EnumManaType.ATTACK) && sameLikeOwner)
            event.setCanceled(true);

        if(event.getManaType().equals(EnumManaType.DEBUFF) && sameLikeOwner)
            event.setCanceled(true);

        if(event.getManaType().equals(EnumManaType.HIT) && sameLikeOwner)
            event.setCanceled(true);
    }
}
