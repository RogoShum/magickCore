package com.rogoshum.magickcore.common.event;

import com.rogoshum.magickcore.common.advancements.StringTrigger;
import com.rogoshum.magickcore.common.init.ModItems;
import com.rogoshum.magickcore.common.lib.LibAdvancements;
import com.rogoshum.magickcore.common.lib.LibEffect;
import com.rogoshum.magickcore.common.lib.LibElements;
import com.rogoshum.magickcore.common.extradata.ExtraDataUtil;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Items;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class AdvancementsEvent {
    public static final StringTrigger STRING_TRIGGER = CriteriaTriggers.register(new StringTrigger());

    @SubscribeEvent
    public void onLivingUpdate(LivingEvent.LivingUpdateEvent event) {
        if(event.getEntityLiving() instanceof ServerPlayer) {
            ServerPlayer player = ((ServerPlayer)event.getEntityLiving());
            STRING_TRIGGER.trigger(player, LibAdvancements.UNLOCK_ROOT);

            player.getActiveEffects().forEach((effectInstance) -> {
                if(effectInstance.getDescriptionId().equals("effect.magickcore." + LibEffect.MANA_FORCE))
                    AdvancementsEvent.STRING_TRIGGER.trigger(player, LibAdvancements.FORCE);
                else if(effectInstance.getDescriptionId().equals("effect.magickcore." + LibEffect.MANA_CONSUM_REDUCE))
                    AdvancementsEvent.STRING_TRIGGER.trigger(player, LibAdvancements.M_CONSUME);
                else if(effectInstance.getDescriptionId().equals("effect.magickcore." + LibEffect.MANA_REGEN))
                    AdvancementsEvent.STRING_TRIGGER.trigger(player, LibAdvancements.M_REGEN);
                else if(effectInstance.getDescriptionId().equals("effect.magickcore." + LibEffect.SHIELD_REGEN))
                    AdvancementsEvent.STRING_TRIGGER.trigger(player, LibAdvancements.S_REGEN);
                else if(effectInstance.getDescriptionId().equals("effect.magickcore." + LibEffect.SHIELD_VALUE))
                    AdvancementsEvent.STRING_TRIGGER.trigger(player, LibAdvancements.S_CAPACITY);
                else if(effectInstance.getDescriptionId().equals("effect.magickcore." + LibEffect.TRACE))
                    AdvancementsEvent.STRING_TRIGGER.trigger(player, LibAdvancements.TRACE);
                else if(effectInstance.getDescriptionId().equals("effect.magickcore." + LibEffect.CHAOS_THEOREM))
                    AdvancementsEvent.STRING_TRIGGER.trigger(player, LibAdvancements.CHAOS);
                else if(effectInstance.getDescriptionId().equals("effect.magickcore." + LibEffect.MANA_CONVERT))
                    AdvancementsEvent.STRING_TRIGGER.trigger(player, LibAdvancements.CONVERT);
                else if(effectInstance.getDescriptionId().equals("effect.magickcore." + LibEffect.MANA_RANGE))
                    AdvancementsEvent.STRING_TRIGGER.trigger(player, LibAdvancements.RANGE);
                else if(effectInstance.getDescriptionId().equals("effect.magickcore." + LibEffect.MANA_TICK))
                    AdvancementsEvent.STRING_TRIGGER.trigger(player, LibAdvancements.TICK);
                else if(effectInstance.getDescriptionId().equals("effect.magickcore." + LibEffect.MULTI_RELEASE))
                    AdvancementsEvent.STRING_TRIGGER.trigger(player, LibAdvancements.MULTI);
            });

            player.getInventory().items.forEach(itemStack -> {
                String name = itemStack.getItem().getRegistryName() != null ? itemStack.getItem().getRegistryName().toString() : "";
                if(name.contains(Items.POTION.toString()))
                    AdvancementsEvent.STRING_TRIGGER.trigger(player, LibAdvancements.POTION_TYPE);
                if(name.contains(Items.DRAGON_BREATH.toString()))
                    AdvancementsEvent.STRING_TRIGGER.trigger(player, LibAdvancements.DRAGON_BREATH);
                else if(name.contains(Items.GLOWSTONE_DUST.toString()))
                    AdvancementsEvent.STRING_TRIGGER.trigger(player, LibAdvancements.GLOW_STONE_DUST);
                else if(name.contains(Items.REDSTONE.toString()))
                    AdvancementsEvent.STRING_TRIGGER.trigger(player, LibAdvancements.RED_STONE);
                else if(name.contains(Items.ROTTEN_FLESH.toString()))
                    AdvancementsEvent.STRING_TRIGGER.trigger(player, LibAdvancements.MEAT);
                else if(name.contains(Items.GUNPOWDER.toString()))
                    AdvancementsEvent.STRING_TRIGGER.trigger(player, LibAdvancements.GUN_POWDER);
                else if(name.contains(Items.NETHER_WART.toString()))
                    AdvancementsEvent.STRING_TRIGGER.trigger(player, LibAdvancements.WART);
                else if(name.contains(Items.SPIDER_EYE.toString()))
                    AdvancementsEvent.STRING_TRIGGER.trigger(player, LibAdvancements.SPIDER_EYE);
                else if(name.contains(Items.BONE.toString()))
                    AdvancementsEvent.STRING_TRIGGER.trigger(player, LibAdvancements.BONE);
                else if(name.contains(Items.BLAZE_ROD.toString()))
                    AdvancementsEvent.STRING_TRIGGER.trigger(player, LibAdvancements.BLAZE_ROD);
                else if(itemStack.getItem() == ModItems.ENDER_DRAGON_MATERIAL.get())
                    AdvancementsEvent.STRING_TRIGGER.trigger(player, LibAdvancements.ENDER_DRAGON_MATERIAL);
                else if(itemStack.getItem() == Items.NETHER_STAR)
                    AdvancementsEvent.STRING_TRIGGER.trigger(player, LibAdvancements.NETHER_STAR_MATERIAL);
                else if(name.contains(Items.ENDER_PEARL.toString()))
                    AdvancementsEvent.STRING_TRIGGER.trigger(player, LibAdvancements.POSITION_MEMORY);
                else if(name.contains(Items.IRON_INGOT.toString()))
                    AdvancementsEvent.STRING_TRIGGER.trigger(player, LibAdvancements.OFFSET_MEMORY);
                else if(name.contains(Items.GOLD_INGOT.toString()))
                    AdvancementsEvent.STRING_TRIGGER.trigger(player, LibAdvancements.DIRECTION_MEMORY);
                else if(name.contains(Items.COAL.toString()))
                    AdvancementsEvent.STRING_TRIGGER.trigger(player, LibAdvancements.CONDITION_NON_LIVING);
                else if(name.contains(Items.FEATHER.toString()))
                    AdvancementsEvent.STRING_TRIGGER.trigger(player, LibAdvancements.CONDITION_LIVING);
                else if(name.contains(Items.COBBLESTONE.toString()))
                    AdvancementsEvent.STRING_TRIGGER.trigger(player, LibAdvancements.CONDITION_BLOCK);
                else if(name.contains(Items.DIAMOND.toString()))
                    AdvancementsEvent.STRING_TRIGGER.trigger(player, LibAdvancements.COMPLETELY_SELF);
                else if(name.contains(Items.GOLD_NUGGET.toString()))
                    AdvancementsEvent.STRING_TRIGGER.trigger(player, LibAdvancements.REVERSE);
                else if(name.contains(Items.FLINT.toString()))
                    AdvancementsEvent.STRING_TRIGGER.trigger(player, LibAdvancements.FORM_SEPARATOR);
            });

            ExtraDataUtil.entityStateData(player, state -> {
                if (!state.getElement().type().equals(LibElements.ORIGIN) && event.getEntityLiving() instanceof Player) {
                    AdvancementsEvent.STRING_TRIGGER.trigger(player, state.getElement().type());
                }
            });
        }
    }
}
