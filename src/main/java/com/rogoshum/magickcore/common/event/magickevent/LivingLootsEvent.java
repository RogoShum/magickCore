package com.rogoshum.magickcore.common.event.magickevent;

import com.rogoshum.magickcore.api.event.living.LivingDeathEvent;
import com.rogoshum.magickcore.common.entity.ManaItemEntity;
import com.rogoshum.magickcore.common.entity.projectile.ManaElementOrbEntity;
import com.rogoshum.magickcore.common.extradata.ExtraDataUtil;
import com.rogoshum.magickcore.common.extradata.entity.EntityStateData;
import com.rogoshum.magickcore.common.init.ModEntities;
import com.rogoshum.magickcore.common.init.ModItems;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import com.rogoshum.magickcore.common.event.SubscribeEvent;
import net.minecraft.world.phys.Vec3;

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
            ManaItemEntity mana = new ManaItemEntity(event.getEntityLiving().level, event.getEntityLiving().getX(), event.getEntityLiving().getY(), event.getEntityLiving().getZ(), livingLoots.get(event.getEntityLiving().getType()));
            event.getEntityLiving().level.addFreshEntity(mana);
            mana.setExtendedLifetime();
        }

        EntityStateData state = ExtraDataUtil.entityStateData(event.getEntityLiving());
        if(state != null) {
            ManaElementOrbEntity orb = new ManaElementOrbEntity(ModEntities.ELEMENT_ORB.get(), event.getEntityLiving().level);
            Vec3 vec = event.getEntityLiving().position();
            orb.setPos(vec.x, vec.y + event.getEntityLiving().getBbHeight() / 2, vec.z);
            orb.spellContext().element(state.getElement());
            orb.setOrbType(true);
            orb.manaCapacity().setMana((event.getEntityLiving()).getMaxHealth());
            orb.spellContext().tick(200);
            orb.setOwner(event.getEntityLiving());
            event.getEntityLiving().level.addFreshEntity(orb);
        }
    }
}
