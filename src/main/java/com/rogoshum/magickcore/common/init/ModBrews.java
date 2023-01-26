package com.rogoshum.magickcore.common.init;

import com.rogoshum.magickcore.mixin.fabric.registry.PrivateUtil;
import net.minecraft.core.Registry;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.item.alchemy.Potions;

public class ModBrews {

    public static void registryBrewing() {
        Potion nothing = ModEffects.NOTHING.get();
        Potion SHIELD_REGEN = ModEffects.SHIELD_REGEN_P.get();
        Potion SHIELD_VALUE = ModEffects.SHIELD_VALUE_P.get();
        Potion MANA_REGEN = ModEffects.MANA_REGEN_P.get();
        Potion MANA_CONSUME_REDUCE = ModEffects.MANA_CONSUM_REDUCE_P.get();
        Potion MANA_FORCE = ModEffects.MANA_FORCE_P.get();
        Potion MANA_TICK = ModEffects.MANA_TICK_P.get();
        Potion MANA_RANGE = ModEffects.MANA_RANGE_P.get();
        //ItemStack MANA_MULTI_CAST = PotionUtils.addPotionToItemStack(new ItemStack(Items.POTION), ModEffects.MULTI_RELEASE_P.get());
        Potion MANA_CHAOS = ModEffects.CHAOS_THEOREM_P.get();
        Potion MANA_CONVERT = ModEffects.MANA_CONVERT_P.get();
        Registry.ITEM.forEach(item -> {
            String name = item.getDescriptionId();
            if(name.contains("spirit_crystal"))
                PrivateUtil.registerPotionBrewing(Potions.WATER, item, ModEffects.NOTHING.get());
            else if(name.contains("dragon_breath"))
                PrivateUtil.registerPotionBrewing(nothing, item, ModEffects.TRACE_P.get());
            else if(name.contains("shulker"))
                PrivateUtil.registerPotionBrewing(nothing, item, SHIELD_REGEN);
            else if(name.contains("scute"))
                PrivateUtil.registerPotionBrewing(nothing, item, SHIELD_VALUE);
            else if(name.contains("golden_carrot"))
                PrivateUtil.registerPotionBrewing(nothing, item, MANA_CONSUME_REDUCE);
            else if(name.contains("nautilus"))
                PrivateUtil.registerPotionBrewing(nothing, item, MANA_REGEN);
            else if(name.contains("netherite"))
                PrivateUtil.registerPotionBrewing(nothing, item, MANA_FORCE);
            else if(name.contains("rabbit_foot"))
                PrivateUtil.registerPotionBrewing(nothing, item, MANA_RANGE);
            else if(name.contains("sugar"))
                PrivateUtil.registerPotionBrewing(nothing, item, MANA_TICK);
            //else if(name.contains("pufferfish"))
                //(PrivateUtil).registerPotionBrewing();(nothing, item, MANA_MULTI_CAST));
            else if(name.contains("phantom_membrane"))
                PrivateUtil.registerPotionBrewing(nothing, item, MANA_CHAOS);
            else if(name.contains("blaze_powder"))
                PrivateUtil.registerPotionBrewing(nothing, item, MANA_CONVERT);
            else if(name.contains("redstone")) {
                PrivateUtil.registerPotionBrewing(SHIELD_REGEN, item, ModEffects.SHIELD_REGEN_P_I.get());
                PrivateUtil.registerPotionBrewing(SHIELD_VALUE, item, ModEffects.SHIELD_VALUE_P_I.get());
                PrivateUtil.registerPotionBrewing(MANA_CONSUME_REDUCE, item, ModEffects.MANA_CONSUM_REDUCE_P_I.get());
                PrivateUtil.registerPotionBrewing(MANA_REGEN, item, ModEffects.MANA_REGEN_P_I.get());
                PrivateUtil.registerPotionBrewing(MANA_FORCE, item, ModEffects.MANA_FORCE_P_I.get());
                PrivateUtil.registerPotionBrewing(MANA_RANGE, item, ModEffects.MANA_RANGE_P_I.get());
                PrivateUtil.registerPotionBrewing(MANA_TICK, item, ModEffects.MANA_TICK_P_I.get());
            }
            else if(name.contains("glowstone")) {
                PrivateUtil.registerPotionBrewing(SHIELD_REGEN, item, ModEffects.SHIELD_REGEN_P_II.get());
                PrivateUtil.registerPotionBrewing(SHIELD_VALUE, item, ModEffects.SHIELD_VALUE_P_II.get());
                PrivateUtil.registerPotionBrewing(MANA_CONSUME_REDUCE, item, ModEffects.MANA_CONSUM_REDUCE_P_II.get());
                PrivateUtil.registerPotionBrewing(MANA_REGEN, item, ModEffects.MANA_REGEN_P_II.get());
                PrivateUtil.registerPotionBrewing(MANA_FORCE, item, ModEffects.MANA_FORCE_P_II.get());
                PrivateUtil.registerPotionBrewing(MANA_RANGE, item, ModEffects.MANA_RANGE_P_II.get());
                PrivateUtil.registerPotionBrewing(MANA_TICK, item, ModEffects.MANA_TICK_P_II.get());
                //(PrivateUtil).registerPotionBrewing();(Potionitem.fromStacks(MANA_MULTI_CAST), item, PotionUtils.addPotionToItemStack(new ItemStack(Items.POTION), ModEffects.MULTI_RELEASE_P_I.get());
                PrivateUtil.registerPotionBrewing(MANA_CONVERT, item, ModEffects.MANA_CONVERT_P_I.get());
            }
        });
    }
}
