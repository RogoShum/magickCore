package com.rogoshum.magickcore.init;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.lib.LibEffect;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.EffectType;
import net.minecraft.potion.Potion;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;

public class ModEffects {
    public static final DeferredRegister<Effect> EFFECTS = DeferredRegister.create(ForgeRegistries.POTIONS, MagickCore.MOD_ID);
    public static RegistryObject<Effect> SHIELD_REGEN = EFFECTS.register(LibEffect.SHIELD_REGEN, () -> new ModEffect(EffectType.BENEFICIAL, 2706137));
    public static RegistryObject<Effect> SHIELD_VALUE = EFFECTS.register(LibEffect.SHIELD_VALUE, () -> new ModEffect(EffectType.BENEFICIAL, 2706038));
    public static RegistryObject<Effect> MANA_CONSUM_REDUCE = EFFECTS.register(LibEffect.MANA_CONSUM_REDUCE, () -> new ModEffect(EffectType.BENEFICIAL, 8407733));
    public static RegistryObject<Effect> MANA_REGEN = EFFECTS.register(LibEffect.MANA_REGEN, () -> new ModEffect(EffectType.BENEFICIAL, 11291317));
    public static RegistryObject<Effect> TRACE = EFFECTS.register(LibEffect.TRACE, () -> new ModEffect(EffectType.BENEFICIAL, 14568046));
    public static RegistryObject<Effect> MANA_FORCE = EFFECTS.register(LibEffect.MANA_FORCE, () -> new ModEffect(EffectType.BENEFICIAL, 14585018));
    public static RegistryObject<Effect> MANA_STASIS = EFFECTS.register(LibEffect.MANA_STASIS, () -> new ModEffect(EffectType.HARMFUL, 	6619040));

    public static final DeferredRegister<Potion> POTIONS = DeferredRegister.create(ForgeRegistries.POTION_TYPES, MagickCore.MOD_ID);
    public static RegistryObject<Potion> SHIELD_REGEN_P = POTIONS.register(LibEffect.SHIELD_REGEN, () -> new Potion(new EffectInstance(SHIELD_REGEN.get(), 3000, 0)));
    public static RegistryObject<Potion> SHIELD_REGEN_P_II = POTIONS.register(LibEffect.SHIELD_REGEN_II, () -> new Potion(new EffectInstance(SHIELD_REGEN.get(), 1200, 1)));
    public static RegistryObject<Potion> SHIELD_VALUE_P = POTIONS.register(LibEffect.SHIELD_VALUE, () -> new Potion(new EffectInstance(SHIELD_VALUE.get(), 7200, 0)));
    public static RegistryObject<Potion> SHIELD_VALUE_P_II = POTIONS.register(LibEffect.SHIELD_VALUE_II, () -> new Potion(new EffectInstance(SHIELD_VALUE.get(), 3000, 1)));
    public static RegistryObject<Potion> MANA_CONSUM_REDUCE_P = POTIONS.register(LibEffect.MANA_CONSUM_REDUCE, () -> new Potion(new EffectInstance(MANA_CONSUM_REDUCE.get(), 3000, 0)));
    public static RegistryObject<Potion> MANA_CONSUM_REDUCE_P_II = POTIONS.register(LibEffect.MANA_CONSUM_REDUCE_II, () -> new Potion(new EffectInstance(MANA_CONSUM_REDUCE.get(), 1200, 1)));
    public static RegistryObject<Potion> MANA_REGEN_P = POTIONS.register(LibEffect.MANA_REGEN, () -> new Potion(new EffectInstance(MANA_REGEN.get(), 4800, 0)));
    public static RegistryObject<Potion> MANA_REGEN_P_II = POTIONS.register(LibEffect.MANA_REGEN_II, () -> new Potion(new EffectInstance(MANA_REGEN.get(), 1500, 1)));
    public static RegistryObject<Potion> TRACE_P = POTIONS.register(LibEffect.TRACE, () -> new Potion(new EffectInstance(TRACE.get(), 4800, 0)));
    public static RegistryObject<Potion> MANA_FORCE_P = POTIONS.register(LibEffect.MANA_FORCE, () -> new Potion(new EffectInstance(MANA_FORCE.get(), 4800, 0)));

    public static class ModEffect extends Effect
    {
        protected ModEffect(EffectType typeIn, int liquidColorIn) {
            super(typeIn, liquidColorIn);
        }

        @Override
        public void affectEntity(@Nullable Entity source, @Nullable Entity indirectSource, LivingEntity entityLivingBaseIn, int amplifier, double health) {

        }

        @Override
        public void performEffect(LivingEntity entityLivingBaseIn, int amplifier) {

        }
    }
}
