package com.rogoshum.magickcore.common.init;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.common.lib.LibEffect;
import com.rogoshum.magickcore.common.registry.DeferredRegister;
import com.rogoshum.magickcore.common.registry.RegistryObject;
import net.minecraft.core.Registry;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.alchemy.Potion;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class ModEffects {
    public static final DeferredRegister<MobEffect> EFFECTS = DeferredRegister.create(Registry.MOB_EFFECT, MagickCore.MOD_ID);
    public static RegistryObject<MobEffect> SHIELD_REGEN = EFFECTS.register(LibEffect.SHIELD_REGEN, () -> new ModEffect(MobEffectCategory.BENEFICIAL, 2706137));
    public static RegistryObject<MobEffect> SHIELD_VALUE = EFFECTS.register(LibEffect.SHIELD_VALUE, () -> new ModEffect(MobEffectCategory.BENEFICIAL, 2706038));
    public static RegistryObject<MobEffect> MANA_CONSUME_REDUCE = EFFECTS.register(LibEffect.MANA_CONSUM_REDUCE, () -> new ModEffect(MobEffectCategory.BENEFICIAL, 8407733));
    public static RegistryObject<MobEffect> MANA_REGEN = EFFECTS.register(LibEffect.MANA_REGEN, () -> new ModEffect(MobEffectCategory.BENEFICIAL, 11291317));
    public static RegistryObject<MobEffect> TRACE = EFFECTS.register(LibEffect.TRACE, () -> new ModEffect(MobEffectCategory.BENEFICIAL, 14568046));
    public static RegistryObject<MobEffect> MANA_FORCE = EFFECTS.register(LibEffect.MANA_FORCE, () -> new ModEffect(MobEffectCategory.BENEFICIAL, 6203391));
    public static RegistryObject<MobEffect> MANA_RANGE = EFFECTS.register(LibEffect.MANA_RANGE, () -> new ModEffect(MobEffectCategory.BENEFICIAL, 16711680));
    public static RegistryObject<MobEffect> MANA_TICK = EFFECTS.register(LibEffect.MANA_TICK, () -> new ModEffect(MobEffectCategory.BENEFICIAL, 16776960));
    //public static RegistryObject<Effect> MULTI_RELEASE = EFFECTS.register(LibEffect.MULTI_RELEASE, () -> new ModEffect(MobEffectCategory.BENEFICIAL, 15567104));
    public static RegistryObject<MobEffect> CHAOS_THEOREM = EFFECTS.register(LibEffect.CHAOS_THEOREM, () -> new ModEffect(MobEffectCategory.BENEFICIAL, 28672));
    public static RegistryObject<MobEffect> MANA_CONVERT = EFFECTS.register(LibEffect.MANA_CONVERT, () -> new ModEffect(MobEffectCategory.BENEFICIAL, 6608630));
    public static RegistryObject<MobEffect> MANA_STASIS = EFFECTS.register(LibEffect.MANA_STASIS, () -> new ModEffect(MobEffectCategory.HARMFUL, 	6619040));

    public static final DeferredRegister<Potion> POTIONS = DeferredRegister.create(Registry.POTION, MagickCore.MOD_ID);
    public static RegistryObject<Potion> SHIELD_REGEN_P = POTIONS.register(LibEffect.SHIELD_REGEN, () -> new Potion(new MobEffectInstance(SHIELD_REGEN.get(), 1200, 0)));
    public static RegistryObject<Potion> SHIELD_REGEN_P_I = POTIONS.register(LibEffect.SHIELD_REGEN_I, () -> new Potion(new MobEffectInstance(SHIELD_REGEN.get(), 3000, 0)));
    public static RegistryObject<Potion> SHIELD_REGEN_P_II = POTIONS.register(LibEffect.SHIELD_REGEN_II, () -> new Potion(new MobEffectInstance(SHIELD_REGEN.get(), 1200, 1)));
    public static RegistryObject<Potion> SHIELD_VALUE_P = POTIONS.register(LibEffect.SHIELD_VALUE, () -> new Potion(new MobEffectInstance(SHIELD_VALUE.get(), 3000, 0)));
    public static RegistryObject<Potion> SHIELD_VALUE_P_I = POTIONS.register(LibEffect.SHIELD_VALUE_I, () -> new Potion(new MobEffectInstance(SHIELD_VALUE.get(), 7200, 0)));
    public static RegistryObject<Potion> SHIELD_VALUE_P_II = POTIONS.register(LibEffect.SHIELD_VALUE_II, () -> new Potion(new MobEffectInstance(SHIELD_VALUE.get(), 3000, 1)));
    public static RegistryObject<Potion> MANA_CONSUM_REDUCE_P = POTIONS.register(LibEffect.MANA_CONSUM_REDUCE, () -> new Potion(new MobEffectInstance(MANA_CONSUME_REDUCE.get(), 1200, 0)));
    public static RegistryObject<Potion> MANA_CONSUM_REDUCE_P_I = POTIONS.register(LibEffect.MANA_CONSUM_REDUCE_I, () -> new Potion(new MobEffectInstance(MANA_CONSUME_REDUCE.get(), 3000, 0)));
    public static RegistryObject<Potion> MANA_CONSUM_REDUCE_P_II = POTIONS.register(LibEffect.MANA_CONSUM_REDUCE_II, () -> new Potion(new MobEffectInstance(MANA_CONSUME_REDUCE.get(), 1200, 1)));
    public static RegistryObject<Potion> MANA_REGEN_P = POTIONS.register(LibEffect.MANA_REGEN, () -> new Potion(new MobEffectInstance(MANA_REGEN.get(), 1500, 0)));
    public static RegistryObject<Potion> MANA_REGEN_P_I = POTIONS.register(LibEffect.MANA_REGEN_I, () -> new Potion(new MobEffectInstance(MANA_REGEN.get(), 4800, 0)));
    public static RegistryObject<Potion> MANA_REGEN_P_II = POTIONS.register(LibEffect.MANA_REGEN_II, () -> new Potion(new MobEffectInstance(MANA_REGEN.get(), 1500, 1)));
    public static RegistryObject<Potion> TRACE_P = POTIONS.register(LibEffect.TRACE, () -> new Potion(new MobEffectInstance(TRACE.get(), 4800, 0)));
    public static RegistryObject<Potion> MANA_FORCE_P = POTIONS.register(LibEffect.MANA_FORCE, () -> new Potion(new MobEffectInstance(MANA_FORCE.get(), 1500, 0)));
    public static RegistryObject<Potion> MANA_FORCE_P_I = POTIONS.register(LibEffect.MANA_FORCE_I, () -> new Potion(new MobEffectInstance(MANA_FORCE.get(), 4800, 0)));
    public static RegistryObject<Potion> MANA_FORCE_P_II = POTIONS.register(LibEffect.MANA_FORCE_II, () -> new Potion(new MobEffectInstance(MANA_FORCE.get(), 1500, 1)));
    public static RegistryObject<Potion> MANA_RANGE_P = POTIONS.register(LibEffect.MANA_RANGE, () -> new Potion(new MobEffectInstance(MANA_RANGE.get(), 1500, 0)));
    public static RegistryObject<Potion> MANA_RANGE_P_I = POTIONS.register(LibEffect.MANA_RANGE_I, () -> new Potion(new MobEffectInstance(MANA_RANGE.get(), 4800, 0)));
    public static RegistryObject<Potion> MANA_RANGE_P_II = POTIONS.register(LibEffect.MANA_RANGE_II, () -> new Potion(new MobEffectInstance(MANA_RANGE.get(), 1500, 1)));
    public static RegistryObject<Potion> MANA_TICK_P = POTIONS.register(LibEffect.MANA_TICK, () -> new Potion(new MobEffectInstance(MANA_TICK.get(), 1500, 0)));
    public static RegistryObject<Potion> MANA_TICK_P_I = POTIONS.register(LibEffect.MANA_TICK_I, () -> new Potion(new MobEffectInstance(MANA_TICK.get(), 4800, 0)));
    public static RegistryObject<Potion> MANA_TICK_P_II = POTIONS.register(LibEffect.MANA_TICK_II, () -> new Potion(new MobEffectInstance(MANA_TICK.get(), 1500, 1)));
    public static RegistryObject<Potion> CHAOS_THEOREM_P = POTIONS.register(LibEffect.CHAOS_THEOREM, () -> new Potion(new MobEffectInstance(CHAOS_THEOREM.get(), 3000, 0)));
    //public static RegistryObject<Potion> MULTI_RELEASE_P = POTIONS.register(LibEffect.MULTI_RELEASE, () -> new Potion(new MobEffectInstance()(MULTI_RELEASE.get(), 1500, 0)));
    //public static RegistryObject<Potion> MULTI_RELEASE_P_I = POTIONS.register(LibEffect.MULTI_RELEASE_I, () -> new Potion(new MobEffectInstance()(MULTI_RELEASE.get(), 1500, 1)));
    public static RegistryObject<Potion> MANA_CONVERT_P = POTIONS.register(LibEffect.MANA_CONVERT, () -> new Potion(new MobEffectInstance(MANA_CONVERT.get(), 1500, 0)));
    public static RegistryObject<Potion> MANA_CONVERT_P_I = POTIONS.register(LibEffect.MANA_CONVERT_I, () -> new Potion(new MobEffectInstance(MANA_CONVERT.get(), 1500, 1)));

    public static RegistryObject<Potion> NOTHING = POTIONS.register(LibEffect.NOTHING, Potion::new);
    public static final List<MobEffect> effectList = new ArrayList<>();

    public static class ModEffect extends MobEffect {
        protected ModEffect(MobEffectCategory typeIn, int liquidColorIn) {
            super(typeIn, liquidColorIn);
            effectList.add(this);
        }

        @Override
        public void applyInstantenousEffect(@Nullable Entity source, @Nullable Entity indirectSource, LivingEntity entityLivingBaseIn, int amplifier, double health) {

        }

        @Override
        public void applyEffectTick(LivingEntity entityLivingBaseIn, int amplifier) {

        }
    }
}
