package com.rogoshum.magickcore.common.event.magickevent;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.common.event.SubscribeEvent;
import com.rogoshum.magickcore.common.init.ModEntities;
import net.minecraft.world.entity.LivingEntity;

public class LivingAttributeEvent {
    @SubscribeEvent
    public static void registerEntityAttributes(EntityAttributeCreationEvent event) {
        event.put(ModEntities.MAGE.get(), VillagerEntity.createAttributes().build());
        event.put(ModEntities.ARTIFICIAL_LIFE.get(), LivingEntity.createLivingAttributes().build());
    }
}
