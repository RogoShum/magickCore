package com.rogoshum.magickcore.event;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.api.event.ElementEvent;
import com.rogoshum.magickcore.api.event.ExtraDataEvent;
import com.rogoshum.magickcore.enums.EnumApplyType;
import com.rogoshum.magickcore.lib.LibElements;
import com.rogoshum.magickcore.lib.LibEntityData;
import com.rogoshum.magickcore.lib.LibRegistry;
import com.rogoshum.magickcore.magick.MagickReleaseHelper;
import com.rogoshum.magickcore.magick.ability.*;
import com.rogoshum.magickcore.magick.extradata.entity.ElementToolData;
import com.rogoshum.magickcore.magick.extradata.entity.EntityStateData;
import com.rogoshum.magickcore.magick.extradata.entity.TakenEntityData;
import com.rogoshum.magickcore.magick.extradata.item.ItemManaData;
import com.rogoshum.magickcore.registry.elementmap.ElementFunctions;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class RegisterEvent {

    @SubscribeEvent
    public void entityExtraData(ExtraDataEvent.Entity event) {
        event.add(LibEntityData.ENTITY_STATE, EntityStateData::new);
        event.add(LibEntityData.ELEMENT_TOOL, ElementToolData::new);
        event.add(LibEntityData.TAKEN_ENTITY, TakenEntityData::new);
    }

    @SubscribeEvent
    public void itemExtraData(ExtraDataEvent.ItemStack event) {
        event.add(LibRegistry.ITEM_DATA, ItemManaData::new);
    }
}
