package com.rogoshum.magickcore.common.magick.ability;

import com.rogoshum.magickcore.common.api.enums.ApplyType;
import com.rogoshum.magickcore.common.extradata.entity.ElementToolData;
import com.rogoshum.magickcore.common.entity.projectile.PhantomEntity;
import com.rogoshum.magickcore.common.init.ModEntities;
import com.rogoshum.magickcore.common.lib.LibContext;
import com.rogoshum.magickcore.common.magick.MagickReleaseHelper;
import com.rogoshum.magickcore.common.init.ModBuff;
import com.rogoshum.magickcore.common.init.ModDamage;
import com.rogoshum.magickcore.common.lib.LibBuff;
import com.rogoshum.magickcore.common.lib.LibElements;
import com.rogoshum.magickcore.common.magick.context.MagickContext;
import com.rogoshum.magickcore.common.magick.context.child.PositionContext;
import com.rogoshum.magickcore.common.magick.context.child.SpawnContext;
import com.rogoshum.magickcore.common.extradata.ExtraDataUtil;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;

import java.util.List;

public class StasisAbility{
    public static boolean hitEntity(MagickContext context) {
        if(context.victim == null) return false;
        return ModBuff.applyBuff(context.victim, LibBuff.SLOW, context.tick, context.force, false);
    }

    public static boolean damageEntity(MagickContext context) {
        if(context.victim == null) return false;
        if(ModBuff.hasBuff(context.victim, LibBuff.SLOW))
            context.force *= 1.5;

        boolean flag = false;
        if(context.caster != null && context.projectile != null)
            flag = context.victim.attackEntityFrom(ModDamage.applyProjectileStasisDamage(context.caster, context.projectile), context.force);
        else if(context.caster != null)
            flag = context.victim.attackEntityFrom(ModDamage.applyEntityStasisDamage(context.caster), context.force);
        else if(context.projectile != null)
            flag = context.victim.attackEntityFrom(ModDamage.applyEntityStasisDamage(context.projectile), context.force);
        else
            flag = context.victim.attackEntityFrom(ModDamage.getStasisDamage(), context.force);
        if(flag)
            ModBuff.applyBuff(context.victim, LibBuff.FREEZE, context.tick / 8, 0, false);

        return flag;
    }

    public static boolean hitBlock(MagickContext context) {
        if(!context.world.isRemote && context.containChild(LibContext.POSITION)) {
            PositionContext positionContext = context.getChild(LibContext.POSITION);

            BlockPos pos = new BlockPos(positionContext.pos);
            if (context.world.getBlockState(pos).getBlock().equals(Blocks.WATER.getBlock())) {
                context.world.setBlockState(pos, Blocks.ICE.getDefaultState());
                return true;
            }

            if (context.world.isAirBlock(pos.add(0, 1, 0)) && Blocks.SNOW.getDefaultState().isValidPosition(context.world, pos.add(0, 1, 0))) {
                context.world.setBlockState(pos.add(0, 1, 0), Blocks.SNOW.getDefaultState(), 2);
                return true;
            }
        }
        return false;
    }

    public static boolean applyBuff(MagickContext context) {
        if(context.victim == null) return false;
        return ModBuff.applyBuff(context.victim, LibBuff.STASIS, context.tick * 2, context.force, true);
    }

    public static boolean applyDebuff(MagickContext context) {
        if(context.victim == null) return false;
        if(context.force >= 7)
            return ModBuff.applyBuff(context.victim, LibBuff.FREEZE, context.tick, context.force, false);
        return ModBuff.applyBuff(context.victim, LibBuff.SLOW, context.tick, context.force, false);
    }

    public static boolean applyToolElement(MagickContext context) {
        int level = (int) context.force;
        if(!(context.caster instanceof LivingEntity)) return false;
        LivingEntity entity = (LivingEntity) context.caster;

        boolean worked = false;
        List<Entity> list = entity.world.getEntitiesWithinAABBExcludingEntity(entity, entity.getBoundingBox().grow(level * 2));
        for (Entity entity1 : list) {
            Vector3d dir = entity1.getPositionVec().add(0, entity1.getHeight() * 0.5, 0).subtract(entity.getPositionVec().add(0, entity.getHeight() * 0.5, 0)).normalize();

            if(dir.dotProduct(entity.getLookVec()) > 0.3 && !MagickReleaseHelper.sameLikeOwner(entity, entity1)) {
                entity1.setMotion(entity1.getMotion().scale(Math.pow(0.7, level)));
                worked = true;
            }
        }

        if(worked && entity.ticksExisted % 20 == 0) {
            ElementToolData tool = ExtraDataUtil.elementToolData(entity);
            if (tool != null) {
                tool.consumeElementOnTool(entity, LibElements.STASIS);
            }
        }
        return true;
    }

    public static boolean superEntity(MagickContext context) {
        if(context.caster == null) return false;
        PositionContext positionContext = PositionContext.create(context.caster.getPositionVec().add(0, 2, 0));
        context.addChild(positionContext);
        SpawnContext spawnContext = new SpawnContext();
        spawnContext.entityType = ModEntities.SILENCE_SQUALL.get();
        context.addChild(spawnContext);
        context.applyType(ApplyType.SPAWN_ENTITY);
        return MagickReleaseHelper.releaseMagick(context);
    }

    public static boolean agglomerate(MagickContext context) {
        if(!(context.victim instanceof LivingEntity) || context.world.isRemote) return false;
        PhantomEntity phantom = new PhantomEntity(ModEntities.PHANTOM.get(), context.world);
        phantom.setEntity((LivingEntity) context.victim);
        phantom.setPosition(context.victim.getPosX(), context.victim.getPosY(), context.victim.getPosZ());
        phantom.spellContext().tick(context.tick * 2);
        context.world.addEntity(phantom);
        return true;
    }

    public static boolean diffusion(MagickContext context) {
        if(!(context.victim instanceof LivingEntity)) return false;
        return ModBuff.applyBuff(context.victim, LibBuff.PURE, context.tick * 2, context.force, true);
    }
}
