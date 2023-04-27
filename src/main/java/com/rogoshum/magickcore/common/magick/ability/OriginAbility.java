package com.rogoshum.magickcore.common.magick.ability;

import com.rogoshum.magickcore.common.lib.LibContext;
import com.rogoshum.magickcore.api.magick.context.MagickContext;
import com.rogoshum.magickcore.api.magick.context.child.PotionContext;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.EntityDamageSource;
import net.minecraft.world.damagesource.IndirectEntityDamageSource;

public class OriginAbility {
    public static boolean damageEntity(MagickContext attribute) {
        if(attribute.victim == null) return false;
        if(!attribute.victim.isOnGround())
            attribute.force *= 2;
        if(attribute.caster != null && attribute.projectile instanceof Projectile)
            return attribute.victim.hurt(new IndirectEntityDamageSource(DamageSource.MAGIC.getMsgId(), attribute.projectile, attribute.caster), attribute.force);
        else if(attribute.caster != null)
            return attribute.victim.hurt(new EntityDamageSource(DamageSource.MAGIC.getMsgId(), attribute.caster), attribute.force);
        else if(attribute.projectile != null)
            return attribute.victim.hurt(new EntityDamageSource(DamageSource.MAGIC.getMsgId(), attribute.projectile), attribute.force);
        else
            return attribute.victim.hurt(DamageSource.MAGIC, attribute.force);
    }

    public static boolean potion(MagickContext context) {
        if(!context.containChild(LibContext.POTION) || !(context.victim instanceof LivingEntity)) return false;
        PotionContext potionContext = context.getChild(LibContext.POTION);
        for(MobEffectInstance effectInstance : potionContext.effectInstances) {
            CompoundTag tag = effectInstance.save(new CompoundTag());
            tag.putInt("Duration", (int) (effectInstance.getDuration() * context.force));
            MobEffectInstance effect = MobEffectInstance.load(tag);
            if (effect.getEffect().isInstantenous()) {
                effect.getEffect().applyInstantenousEffect(context.caster, context.projectile, (LivingEntity) context.victim, (int) Math.min(effect.getAmplifier(), context.force), 1.0D);
            } else {
                ((LivingEntity) context.victim).addEffect(new MobEffectInstance(effect));
            }
        }
        return !potionContext.effectInstances.isEmpty();
    }
}
