package com.rogoshum.magickcore.common.event.magickevent;

import com.rogoshum.magickcore.common.entity.ManaItemEntity;
import com.rogoshum.magickcore.common.init.ModItems;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.HashMap;

public class LivingLootsEvent {
    private static final HashMap<EntityType<?>, ItemStack> livingLoots = new HashMap<>();

    public static void init(){
        addLoots(EntityType.ENDER_DRAGON, new ItemStack(ModItems.ENDER_DRAGON_MATERIAL.get()));
    }

    public static void addLoots(EntityType<?> type, ItemStack stack) {
        livingLoots.put(type, stack);
    }

    @SubscribeEvent
    public void onDeath(LivingDeathEvent event) {
        if(event.getEntityLiving().world.isRemote()) return;

        if(livingLoots.containsKey(event.getEntityLiving().getType())) {
            ManaItemEntity mana = new ManaItemEntity(event.getEntityLiving().getEntityWorld(), event.getEntityLiving().getPosX(), event.getEntityLiving().getPosY(), event.getEntityLiving().getPosZ(), livingLoots.get(event.getEntityLiving().getType()));
            event.getEntityLiving().getEntityWorld().addEntity(mana);
            mana.setNoDespawn();
        }
    }
}
