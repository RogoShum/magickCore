package com.rogoshum.magickcore.magick.ability;

import com.rogoshum.magickcore.magick.context.MagickContext;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.IndirectEntityDamageSource;

public class OriginElement{
    public static boolean damageEntity(MagickContext attribute) {
        if(attribute.victim == null) return false;
        if(attribute.caster != null && attribute.projectile != null)
            return attribute.victim.attackEntityFrom(new IndirectEntityDamageSource(DamageSource.MAGIC.getDamageType(), attribute.projectile, attribute.caster), attribute.force);
        else if(attribute.caster != null)
            return attribute.victim.attackEntityFrom(new EntityDamageSource(DamageSource.MAGIC.getDamageType(), attribute.caster), attribute.force);
        else if(attribute.projectile != null)
            return attribute.victim.attackEntityFrom(new EntityDamageSource(DamageSource.MAGIC.getDamageType(), attribute.projectile), attribute.force);
        else
            return attribute.victim.attackEntityFrom(DamageSource.MAGIC, attribute.force);
    }
}
