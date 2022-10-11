package com.rogoshum.magickcore.magick.ability;

import com.rogoshum.magickcore.init.ModBuff;
import com.rogoshum.magickcore.init.ModDamage;
import com.rogoshum.magickcore.lib.LibBuff;
import com.rogoshum.magickcore.lib.LibContext;
import com.rogoshum.magickcore.magick.context.MagickContext;
import com.rogoshum.magickcore.magick.context.child.PositionContext;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;

public class SolarAbility{
    public static boolean hitEntity(MagickContext attribute) {
        if(attribute.victim == null) return false;
        attribute.victim.setFire(Math.max(attribute.tick / 10, 20));
        return attribute.victim.getFireTimer() > 0;
    }

    public static boolean damageEntity(MagickContext attribute) {
        if(attribute.victim == null) return false;
        if(attribute.victim.getFireTimer() > 0)
            attribute.force *= 2;

        if(attribute.caster != null && attribute.projectile != null)
            return attribute.victim.attackEntityFrom(ModDamage.applyProjectileSolarDamage(attribute.caster, attribute.projectile), attribute.force);
        else if(attribute.caster != null)
            return attribute.victim.attackEntityFrom(ModDamage.applyEntitySolarDamage(attribute.caster), attribute.force);
        else if(attribute.projectile != null)
            return attribute.victim.attackEntityFrom(ModDamage.applyEntitySolarDamage(attribute.projectile), attribute.force);
        else
            return attribute.victim.attackEntityFrom(ModDamage.getSolarDamage(), attribute.force);
    }

    public static boolean hitBlock(MagickContext attribute) {
        if(!attribute.world.isRemote && attribute.containChild(LibContext.POSITION)) {
            PositionContext context = attribute.getChild(LibContext.POSITION);

            BlockPos pos = new BlockPos(context.pos);
            if (attribute.world.getBlockState(pos).getBlock().equals(Blocks.ICE.getBlock()) || attribute.world.getBlockState(pos).getBlock().equals(Blocks.SNOW.getBlock()) || attribute.world.getBlockState(pos).getBlock().equals(Blocks.SNOW_BLOCK.getBlock()))
                attribute.world.setBlockState(pos, Blocks.WATER.getDefaultState());

            if (attribute.world.isAirBlock(pos.add(0, 1, 0)) && Blocks.FIRE.getDefaultState().isValidPosition(attribute.world, pos.add(0, 1, 0)))
                attribute.world.setBlockState(pos.add(0, 1, 0), Blocks.FIRE.getDefaultState());
        }
        return true;
    }

    public static boolean applyBuff(MagickContext attribute) {
        return ModBuff.applyBuff(attribute.victim, LibBuff.RADIANCE_WELL, attribute.tick, attribute.force, true);
    }

    public static boolean applyDebuff(MagickContext attribute) {
        if(attribute.victim == null) return false;
        if(!attribute.victim.isImmuneToFire()){
            attribute.victim.setFire((int) (attribute.tick * (attribute.force + 1)));
            return attribute.victim.getFireTimer() > 0;
        }
        return false;
    }

    public static void applyToolElement(MagickContext attribute) {

    }
}
