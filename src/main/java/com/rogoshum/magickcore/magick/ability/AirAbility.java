package com.rogoshum.magickcore.magick.ability;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.init.ModBuff;
import com.rogoshum.magickcore.init.ModDamage;
import com.rogoshum.magickcore.lib.LibBuff;
import com.rogoshum.magickcore.lib.LibContext;
import com.rogoshum.magickcore.magick.context.MagickContext;
import com.rogoshum.magickcore.magick.context.child.DirectionContext;
import com.rogoshum.magickcore.magick.context.child.PositionContext;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;

public class AirAbility {
    public static boolean hitEntity(MagickContext context) {
        if(context.victim == null) return false;
        if(context.containChild(LibContext.DIRECTION)) {
            Vector3d dir = context.<DirectionContext>getChild(LibContext.DIRECTION).direction.normalize().scale(0.05 * context.force);
            Vector3d originMotion = context.victim.getMotion();
            context.victim.setMotion(dir.scale(0.8).add(originMotion.scale(0.2)));
            return true;
        }
        return false;
    }

    public static boolean damageEntity(MagickContext context) {
        if(context.victim == null) return false;
        if(!context.victim.isOnGround())
            context.force *= 2;

        if(context.caster != null && context.projectile != null)
            return context.victim.attackEntityFrom(ModDamage.applyProjectileAirDamage(context.caster, context.projectile), context.force);
        else if(context.caster != null)
            return context.victim.attackEntityFrom(ModDamage.applyEntityAirDamage(context.caster), context.force);
        else if(context.projectile != null)
            return context.victim.attackEntityFrom(ModDamage.applyEntityAirDamage(context.projectile), context.force);
        else
            return context.victim.attackEntityFrom(ModDamage.getAirDamage(), context.force);
    }

    public static boolean hitBlock(MagickContext context) {
        return false;
    }

    public static boolean applyBuff(MagickContext context) {
        return false;
    }

    public static boolean applyDebuff(MagickContext context) {
        return false;
    }

    public static boolean applyToolElement(MagickContext context) {
        return false;
    }
}
