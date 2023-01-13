package com.rogoshum.magickcore.common.magick.ability;

import com.rogoshum.magickcore.common.lib.LibContext;
import com.rogoshum.magickcore.common.magick.context.MagickContext;
import com.rogoshum.magickcore.common.magick.context.child.PotionContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.PotionItem;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.IndirectEntityDamageSource;

public class OriginAbility {
    public static boolean damageEntity(MagickContext attribute) {
        if(attribute.victim == null) return false;
        if(!attribute.victim.isOnGround())
            attribute.force *= 2;
        if(attribute.caster != null && attribute.projectile instanceof ProjectileEntity)
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
        for(EffectInstance effectInstance : potionContext.effectInstances) {
            CompoundNBT tag = effectInstance.save(new CompoundNBT());
            tag.putInt("Duration", (int) (effectInstance.getDuration() * context.force));
            EffectInstance effect = EffectInstance.load(tag);
            if (effect.getEffect().isInstantenous()) {
                effect.getEffect().applyInstantenousEffect(context.caster, context.projectile, (LivingEntity) context.victim, (int) Math.min(effect.getAmplifier(), context.force), 1.0D);
            } else {
                ((LivingEntity) context.victim).addEffect(new EffectInstance(effect));
            }
        }
        return !potionContext.effectInstances.isEmpty();
    }
}
