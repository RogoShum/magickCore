package com.rogoshum.magickcore.common.event;

import com.rogoshum.magickcore.common.advancements.StringTrigger;
import com.rogoshum.magickcore.common.init.ModItems;
import com.rogoshum.magickcore.common.item.ElementCrystalItem;
import com.rogoshum.magickcore.common.item.ManaItem;
import com.rogoshum.magickcore.common.item.OrbBottleItem;
import com.rogoshum.magickcore.common.lib.LibAdvancements;
import com.rogoshum.magickcore.common.lib.LibEffect;
import com.rogoshum.magickcore.common.lib.LibElements;
import com.rogoshum.magickcore.common.util.ExtraDataUtil;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Items;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class AdvancementsEvent {
    public static final StringTrigger STRING_TRIGGER = CriteriaTriggers.register(new StringTrigger());

    @SubscribeEvent
    public void onLivingDeath(LivingDeathEvent event)
    {
        if(event.getSource().getTrueSource() instanceof LivingEntity) {
            ExtraDataUtil.entityStateData(event.getSource().getTrueSource(), state -> {
                if (!state.getElement().type().equals(LibElements.ORIGIN) && event.getEntityLiving() instanceof PlayerEntity) {
                    STRING_TRIGGER.trigger((ServerPlayerEntity) event.getEntityLiving(), LibAdvancements.UNLOCK_ROOT);
                }
            });
        }
    }

    @SubscribeEvent
    public void onLivingUpdate(LivingEvent.LivingUpdateEvent event)
    {
        if(event.getEntityLiving() instanceof ServerPlayerEntity) {
            ServerPlayerEntity player = ((ServerPlayerEntity)event.getEntityLiving());
            player.inventory.mainInventory.forEach((item) -> {
                if(item.getItem() instanceof ElementCrystalItem)
                    AdvancementsEvent.STRING_TRIGGER.trigger(player, LibAdvancements.ELEMENT_CRYSTAL);
                else if(item.getItem() instanceof OrbBottleItem)
                    AdvancementsEvent.STRING_TRIGGER.trigger(player, LibAdvancements.ORB_BOTTLE);
                else if(item.getItem() instanceof ManaItem)
                    AdvancementsEvent.STRING_TRIGGER.trigger(player, LibAdvancements.MANA_ITEM);

                else if(item.getItem().getRegistryName().equals(Items.REDSTONE.getRegistryName())) {
                    AdvancementsEvent.STRING_TRIGGER.trigger(player, LibAdvancements.RED_STONE);
                    AdvancementsEvent.STRING_TRIGGER.trigger(player, LibAdvancements.SUPPLIER);
                }
                else if(item.getItem().getRegistryName().equals(Items.NETHER_WART.getRegistryName()))
                    AdvancementsEvent.STRING_TRIGGER.trigger(player, LibAdvancements.WART);
                else if(item.getItem().getRegistryName().equals(Items.DRAGON_BREATH.getRegistryName()))
                    AdvancementsEvent.STRING_TRIGGER.trigger(player, LibAdvancements.DRAGON_BREATH);
                else if(item.getItem().getRegistryName().equals(Items.GLOWSTONE_DUST.getRegistryName()))
                    AdvancementsEvent.STRING_TRIGGER.trigger(player, LibAdvancements.GLOW_STONE_DUST);
                else if(item.getItem().getRegistryName().equals(Items.GUNPOWDER.getRegistryName()))
                    AdvancementsEvent.STRING_TRIGGER.trigger(player, LibAdvancements.GUN_POWDER);
                else if(item.getItem().getRegistryName().equals(Items.SPIDER_EYE.getRegistryName()))
                    AdvancementsEvent.STRING_TRIGGER.trigger(player, LibAdvancements.SPIDER_EYE);
                /*
                else if(item.getItem().getRegistryName().equals(ModItems.magick_supplier.get().getRegistryName()))
                    AdvancementsEvent.STRING_TRIGGER.trigger(player, LibAdvancements.REPEATER);
                else if(item.getItem().getRegistryName().equals(ModItems.magick_repeater.get().getRegistryName())) {
                    AdvancementsEvent.STRING_TRIGGER.trigger(player, LibAdvancements.BARRIER);
                    AdvancementsEvent.STRING_TRIGGER.trigger(player, LibAdvancements.ENTITY_REPEATER);
                    AdvancementsEvent.STRING_TRIGGER.trigger(player, LibAdvancements.ORDINARY_REPEATER);
                    AdvancementsEvent.STRING_TRIGGER.trigger(player, LibAdvancements.ITEM_REPEATER);
                    AdvancementsEvent.STRING_TRIGGER.trigger(player, LibAdvancements.ENTITY_SELECTOR);
                    AdvancementsEvent.STRING_TRIGGER.trigger(player, LibAdvancements.LIVING_SELECTOR);
                    AdvancementsEvent.STRING_TRIGGER.trigger(player, LibAdvancements.POTION_REPEATER);
                    AdvancementsEvent.STRING_TRIGGER.trigger(player, LibAdvancements.MATERIAL_REPEATER);
                    AdvancementsEvent.STRING_TRIGGER.trigger(player, LibAdvancements.MANA_EXTRACT_REPEATER);
                }
                
                 */
                else if(item.getItem().getRegistryName().equals(ModItems.ENDER_DRAGON_MATERIAL.get().getRegistryName()))
                    AdvancementsEvent.STRING_TRIGGER.trigger(player, LibAdvancements.ENDER_DRAGON_MATERIAL);
                else if(item.getItem().getRegistryName().equals(Items.NETHER_STAR.getRegistryName()))
                    AdvancementsEvent.STRING_TRIGGER.trigger(player, LibAdvancements.NETHER_STAR_MATERIAL);
            });

            player.getActivePotionEffects().forEach((effectInstance) -> {
                if(effectInstance.getEffectName().equals("effect.magickcore." + LibEffect.MANA_FORCE))
                    AdvancementsEvent.STRING_TRIGGER.trigger(player, LibAdvancements.FORCE);
                else if(effectInstance.getEffectName().equals("effect.magickcore." + LibEffect.MANA_CONSUM_REDUCE))
                    AdvancementsEvent.STRING_TRIGGER.trigger(player, LibAdvancements.M_CONSUME);
                else if(effectInstance.getEffectName().equals("effect.magickcore." + LibEffect.MANA_REGEN))
                    AdvancementsEvent.STRING_TRIGGER.trigger(player, LibAdvancements.M_REGEN);
                else if(effectInstance.getEffectName().equals("effect.magickcore." + LibEffect.SHIELD_REGEN))
                    AdvancementsEvent.STRING_TRIGGER.trigger(player, LibAdvancements.S_REGEN);
                else if(effectInstance.getEffectName().equals("effect.magickcore." + LibEffect.SHIELD_VALUE))
                    AdvancementsEvent.STRING_TRIGGER.trigger(player, LibAdvancements.S_CAPACITY);
                else if(effectInstance.getEffectName().equals("effect.magickcore." + LibEffect.TRACE))
                    AdvancementsEvent.STRING_TRIGGER.trigger(player, LibAdvancements.TRACE);
            });

            ExtraDataUtil.entityStateData(player, state -> {
                if (!state.getElement().type().equals(LibElements.ORIGIN) && event.getEntityLiving() instanceof PlayerEntity) {
                    AdvancementsEvent.STRING_TRIGGER.trigger(player, state.getElement().type());
                }
            });
        }
    }
}
