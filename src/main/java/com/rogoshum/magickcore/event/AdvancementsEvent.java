package com.rogoshum.magickcore.event;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.advancements.StringTrigger;
import com.rogoshum.magickcore.capability.IEntityState;
import com.rogoshum.magickcore.item.*;
import com.rogoshum.magickcore.lib.LibAdvancements;
import com.rogoshum.magickcore.lib.LibEffect;
import com.rogoshum.magickcore.lib.LibElements;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class AdvancementsEvent {
    public static final StringTrigger STRING_TRIGGER = CriteriaTriggers.register(new StringTrigger());

    @SubscribeEvent
    public void onLivingDeath(LivingDeathEvent event)
    {
        if(event.getSource().getTrueSource() instanceof LivingEntity) {
            IEntityState state = event.getSource().getTrueSource().getCapability(MagickCore.entityState).orElse(null);
            if (!state.getElement().getType().equals(LibElements.ORIGIN) && event.getEntityLiving() instanceof PlayerEntity) {
                STRING_TRIGGER.trigger((ServerPlayerEntity) event.getEntityLiving(), LibAdvancements.UNLOCK_ROOT);
            }
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

                else if(item.getItem() instanceof ManaRedstoneItem)
                    AdvancementsEvent.STRING_TRIGGER.trigger(player, LibAdvancements.RED_STONE);
                else if(item.getItem() instanceof ManaNetherWartItem)
                    AdvancementsEvent.STRING_TRIGGER.trigger(player, LibAdvancements.WART);
                else if(item.getItem() instanceof ManaDragonBreathItem)
                    AdvancementsEvent.STRING_TRIGGER.trigger(player, LibAdvancements.DRAGON_BREATH);
                else if(item.getItem() instanceof ManaGlowstoneItem)
                    AdvancementsEvent.STRING_TRIGGER.trigger(player, LibAdvancements.GLOW_STONE_DUST);
                else if(item.getItem() instanceof ManaGunpowderItem)
                    AdvancementsEvent.STRING_TRIGGER.trigger(player, LibAdvancements.GUN_POWDER);
                else if(item.getItem() instanceof ManaFermentedSpiderEyeItem)
                    AdvancementsEvent.STRING_TRIGGER.trigger(player, LibAdvancements.SPIDER_EYE);
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

            IEntityState state = player.getCapability(MagickCore.entityState).orElse(null);
                AdvancementsEvent.STRING_TRIGGER.trigger(player, state.getElement().getType());
        }
    }
}
