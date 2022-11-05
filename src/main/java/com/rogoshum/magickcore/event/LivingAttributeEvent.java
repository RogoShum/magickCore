package com.rogoshum.magickcore.event;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.init.ModEntities;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = MagickCore.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class LivingAttributeEvent {
    @SubscribeEvent
    public static void registerEntityAttributes(EntityAttributeCreationEvent event) {
        event.put(ModEntities.MAGE.get(), VillagerEntity.registerAttributes().create());
    }
}
