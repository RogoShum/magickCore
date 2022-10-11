package com.rogoshum.magickcore.magick.ability;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.init.ModBuff;
import com.rogoshum.magickcore.init.ModDamage;
import com.rogoshum.magickcore.lib.LibBuff;
import com.rogoshum.magickcore.lib.LibContext;
import com.rogoshum.magickcore.lib.LibElements;
import com.rogoshum.magickcore.magick.context.MagickContext;
import com.rogoshum.magickcore.magick.context.child.ItemContext;
import com.rogoshum.magickcore.tool.NBTTagHelper;

public class WitherAbility{
    public static boolean hitEntity(MagickContext attribute) {
        return ModBuff.applyBuff(attribute.victim, LibBuff.WITHER, attribute.tick, attribute.force, false);
    }

    public static boolean damageEntity(MagickContext attribute) {
        if(attribute.victim == null) return false;
        if(ModBuff.hasBuff(attribute.victim, LibBuff.WITHER))
            attribute.force *= 1.25;

        boolean flag = false;
        if(attribute.caster != null && attribute.projectile != null)
            flag = attribute.victim.attackEntityFrom(ModDamage.applyProjectileWitherDamage(attribute.caster, attribute.projectile), attribute.force);
        else if(attribute.caster != null)
            flag = attribute.victim.attackEntityFrom(ModDamage.applyEntityWitherDamage(attribute.caster), attribute.force);
        else if(attribute.projectile != null)
            flag = attribute.victim.attackEntityFrom(ModDamage.applyEntityWitherDamage(attribute.projectile), attribute.force);
        else
            flag = attribute.victim.attackEntityFrom(ModDamage.getWitherDamage(), attribute.force);

        return flag;
    }

    public static boolean hitBlock(MagickContext attribute) {
        return false;
    }

    public static boolean applyBuff(MagickContext attribute) {
        return ModBuff.applyBuff(attribute.victim, LibBuff.DECAY, attribute.tick, attribute.force, true);
    }

    public static boolean applyDebuff(MagickContext attribute) {
        return ModBuff.applyBuff(attribute.victim, LibBuff.CRIPPLE, attribute.tick, attribute.force, false) && ModBuff.applyBuff(attribute.victim, LibBuff.WITHER, attribute.tick, attribute.force, false);
    }

    public static boolean applyToolElement(MagickContext attribute) {
        if(!attribute.containChild(LibContext.ITEM)) return false;
        ItemContext context = attribute.getChild(LibContext.ITEM);
        if(!context.valid()) return false;
        if(context.itemStack.getDamage() > 0 && MagickCore.rand.nextInt(100) == 0) {
            context.itemStack.setDamage(context.itemStack.getDamage() - 1);
            NBTTagHelper.consumeElementOnTool(context.itemStack, LibElements.WITHER);
        }
        return true;
    }
}
