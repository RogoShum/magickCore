package com.rogoshum.magickcore.common.event.magickevent;

import com.rogoshum.magickcore.common.entity.ManaItemEntity;
import com.rogoshum.magickcore.common.init.ModItems;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import com.rogoshum.magickcore.common.event.SubscribeEvent;

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
        if(event.getEntityLiving().level.isClientSide()) return;

        if(livingLoots.containsKey(event.getEntityLiving().getType())) {
            ManaItemEntity mana = new ManaItemEntity(event.getEntityLiving().getCommandSenderLevel(), event.getEntityLiving().getX(), event.getEntityLiving().getY(), event.getEntityLiving().getZ(), livingLoots.get(event.getEntityLiving().getType()));
            event.getEntityLiving().getCommandSenderLevel().addFreshEntity(mana);
            mana.setExtendedLifetime();
        }
    }
}
