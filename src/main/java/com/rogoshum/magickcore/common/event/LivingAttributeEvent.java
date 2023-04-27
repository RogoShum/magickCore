package com.rogoshum.magickcore.common.event;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.common.init.ModEntities;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.npc.Villager;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = MagickCore.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class LivingAttributeEvent {
    @SubscribeEvent
    public static void registerEntityAttributes(EntityAttributeCreationEvent event) {
        event.put(ModEntities.MAGE.get(), Villager.createAttributes().build());
        event.put(ModEntities.ARTIFICIAL_LIFE.get(), LivingEntity.createLivingAttributes().add(Attributes.MAX_HEALTH, 3.0D).build());
        event.put(ModEntities.QUADRANT_CRYSTAL.get(), LivingEntity.createLivingAttributes().add(Attributes.MAX_HEALTH, 50.0D).build());
        event.put(ModEntities.LIVING_ARGENT.get(), LivingEntity.createLivingAttributes().add(Attributes.MAX_HEALTH, 0.0D).build());
    }
}
