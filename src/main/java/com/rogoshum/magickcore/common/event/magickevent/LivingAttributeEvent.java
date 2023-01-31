package com.rogoshum.magickcore.common.event.magickevent;

import com.rogoshum.magickcore.common.init.ModEntities;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.npc.Villager;

public class LivingAttributeEvent {
    public static void registerEntityAttributes() {
        FabricDefaultAttributeRegistry.register(ModEntities.MAGE.get(), Villager.createAttributes());
        FabricDefaultAttributeRegistry.register(ModEntities.ARTIFICIAL_LIFE.get(), LivingEntity.createLivingAttributes());
    }
}
