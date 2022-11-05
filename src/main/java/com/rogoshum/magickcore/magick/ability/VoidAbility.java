package com.rogoshum.magickcore.magick.ability;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.tool.NBTTagHelper;
import com.rogoshum.magickcore.init.ModBuff;
import com.rogoshum.magickcore.init.ModDamage;
import com.rogoshum.magickcore.lib.LibBuff;
import com.rogoshum.magickcore.lib.LibElements;
import com.rogoshum.magickcore.magick.context.MagickContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;

public class VoidAbility{
    public static boolean hitEntity(MagickContext attribute) {
        return ModBuff.applyBuff(attribute.victim, LibBuff.WEAKEN, attribute.tick, attribute.force, false);
    }

    public static boolean damageEntity(MagickContext attribute) {
        if(attribute.victim == null) return false;
        if(ModBuff.hasBuff(attribute.victim, LibBuff.WEAKEN))
            attribute.force *= 2;

        boolean flag = false;
        if(attribute.caster != null && attribute.projectile != null)
            flag = attribute.victim.attackEntityFrom(ModDamage.applyProjectileVoidDamage(attribute.caster, attribute.projectile), attribute.force);
        else if(attribute.caster != null)
            flag = attribute.victim.attackEntityFrom(ModDamage.applyEntityVoidDamage(attribute.caster), attribute.force);
        else if(attribute.projectile != null)
            flag = attribute.victim.attackEntityFrom(ModDamage.applyEntityVoidDamage(attribute.projectile), attribute.force);
        else
            flag = attribute.victim.attackEntityFrom(ModDamage.getVoidDamage(), attribute.force);
        if(flag)
            ModBuff.applyBuff(attribute.victim, LibBuff.FRAGILE, 10, 0, false);

        return flag;
    }

    public static boolean hitBlock(MagickContext attribute) {
        return false;
    }

    public static boolean applyBuff(MagickContext attribute) {
        return ModBuff.applyBuff(attribute.victim, LibBuff.LIGHT, attribute.tick, attribute.force, true);
    }

    public static boolean applyDebuff(MagickContext attribute) {
        return ModBuff.applyBuff(attribute.victim, LibBuff.FRAGILE, attribute.tick, attribute.force, false);
    }

    public static boolean applyToolElement(MagickContext attribute) {
        int level = (int) attribute.force;
        if(!(attribute.caster instanceof LivingEntity)) return false;
        LivingEntity entity = (LivingEntity) attribute.caster;
        ItemStack stack = entity.getHeldItemMainhand();
        if(stack.hasTag() && NBTTagHelper.hasElementOnTool(stack, LibElements.VOID))
        {
            CompoundNBT tag = NBTTagHelper.getStackTag(stack);
            tag.putInt("VOID_LEVEL", level);
            stack.setTag(tag);
        }
        return true;
    }
}
