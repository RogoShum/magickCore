package com.rogoshum.magickcore.common.magick.ability;

import com.rogoshum.magickcore.common.lib.LibContext;
import com.rogoshum.magickcore.common.magick.context.MagickContext;
import com.rogoshum.magickcore.common.magick.context.child.PotionContext;
import net.minecraft.entity.LivingEntity;
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
        if(attribute.caster != null && attribute.projectile != null)
            return attribute.victim.attackEntityFrom(new IndirectEntityDamageSource(DamageSource.MAGIC.getDamageType(), attribute.projectile, attribute.caster), attribute.force);
        else if(attribute.caster != null)
            return attribute.victim.attackEntityFrom(new EntityDamageSource(DamageSource.MAGIC.getDamageType(), attribute.caster), attribute.force);
        else if(attribute.projectile != null)
            return attribute.victim.attackEntityFrom(new EntityDamageSource(DamageSource.MAGIC.getDamageType(), attribute.projectile), attribute.force);
        else
            return attribute.victim.attackEntityFrom(DamageSource.MAGIC, attribute.force);
    }

    public static boolean potion(MagickContext context) {
        if(!context.containChild(LibContext.POTION) || !(context.victim instanceof LivingEntity)) return false;
        PotionContext potionContext = context.getChild(LibContext.POTION);
        for(EffectInstance effectInstance : potionContext.effectInstances) {
            CompoundNBT tag = effectInstance.write(new CompoundNBT());
            tag.putInt("Duration", (int) (effectInstance.getDuration() * context.force));
            EffectInstance effect = EffectInstance.read(tag);
            if (effect.getPotion().isInstant()) {
                effect.getPotion().affectEntity(context.caster, context.projectile, (LivingEntity) context.victim, (int) Math.min(effect.getAmplifier(), context.force), 1.0D);
            } else {
                ((LivingEntity) context.victim).addPotionEffect(new EffectInstance(effect));
            }
        }
        return !potionContext.effectInstances.isEmpty();
    }
}
