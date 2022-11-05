package com.rogoshum.magickcore.magick.ability;

import com.rogoshum.magickcore.enums.EnumManaLimit;
import com.rogoshum.magickcore.lib.LibContext;
import com.rogoshum.magickcore.magick.MagickReleaseHelper;
import com.rogoshum.magickcore.init.ModBuff;
import com.rogoshum.magickcore.init.ModDamage;
import com.rogoshum.magickcore.lib.LibBuff;
import com.rogoshum.magickcore.lib.LibElements;
import com.rogoshum.magickcore.magick.context.MagickContext;
import com.rogoshum.magickcore.magick.context.child.PositionContext;
import com.rogoshum.magickcore.magick.extradata.entity.ElementToolData;
import com.rogoshum.magickcore.tool.ExtraDataHelper;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.BlockPos;

import java.util.List;

public class StasisAbility{
    public static boolean hitEntity(MagickContext attribute) {
        return ModBuff.applyBuff(attribute.victim, LibBuff.SLOW, attribute.tick, attribute.force, false);
    }

    public static boolean damageEntity(MagickContext attribute) {
        if(attribute.victim == null) return false;
        if(ModBuff.hasBuff(attribute.victim, LibBuff.SLOW))
            attribute.force *= 1.5;

        boolean flag = false;
        if(attribute.caster != null && attribute.projectile != null)
            flag = attribute.victim.attackEntityFrom(ModDamage.applyProjectileStasisDamage(attribute.caster, attribute.projectile), attribute.force);
        else if(attribute.caster != null)
            flag = attribute.victim.attackEntityFrom(ModDamage.applyEntityStasisDamage(attribute.caster), attribute.force);
        else if(attribute.projectile != null)
            flag = attribute.victim.attackEntityFrom(ModDamage.applyEntityStasisDamage(attribute.projectile), attribute.force);
        else
            flag = attribute.victim.attackEntityFrom(ModDamage.getStasisDamage(), attribute.force);
        if(flag)
            ModBuff.applyBuff(attribute.victim, LibBuff.FREEZE, attribute.tick / 8, 0, false);

        return flag;
    }

    public static boolean hitBlock(MagickContext attribute) {
        if(!attribute.world.isRemote && attribute.containChild(LibContext.POSITION)) {
            PositionContext context = attribute.getChild(LibContext.POSITION);

            BlockPos pos = new BlockPos(context.pos);
            if (attribute.world.getBlockState(pos).getBlock().equals(Blocks.WATER.getBlock()))
                attribute.world.setBlockState(pos, Blocks.ICE.getDefaultState());

            if (attribute.world.isAirBlock(pos.add(0, 1, 0)) && Blocks.SNOW.getDefaultState().isValidPosition(attribute.world, pos.add(0, 1, 0)))
                attribute.world.setBlockState(pos.add(0, 1, 0), Blocks.SNOW.getDefaultState(), 2);
        }
        return true;
    }

    public static boolean applyBuff(MagickContext attribute) {
        return ModBuff.applyBuff(attribute.victim, LibBuff.STASIS, attribute.tick, attribute.force, true);
    }

    public static boolean applyDebuff(MagickContext attribute) {
        if(attribute.victim == null) return false;
        if(attribute.force >= 7)
            return ModBuff.applyBuff(attribute.victim, LibBuff.FREEZE, attribute.tick, attribute.force, false);
        return ModBuff.applyBuff(attribute.victim, LibBuff.SLOW, attribute.tick, attribute.force, false);
    }

    public static boolean applyToolElement(MagickContext attribute) {
        int level = (int) attribute.force;
        if(!(attribute.caster instanceof LivingEntity)) return false;
        LivingEntity entity = (LivingEntity) attribute.caster;
        boolean worked = false;
        List<Entity> list = entity.world.getEntitiesWithinAABBExcludingEntity(entity, entity.getBoundingBox().grow(level * 1.5));
        for (Entity entity1 : list)
        {
            if(!MagickReleaseHelper.sameLikeOwner(entity, entity1)) {
                entity1.setMotion(entity1.getMotion().scale(Math.pow(0.85, level)));
                worked = true;
            }
        }

        if(worked && entity.ticksExisted % 15 == 0) {
            ElementToolData tool = ExtraDataHelper.elementToolData(entity);
            if (tool != null) {
                tool.consumeElementOnTool(entity, LibElements.STASIS);
            }
        }
        return true;
    }
}
