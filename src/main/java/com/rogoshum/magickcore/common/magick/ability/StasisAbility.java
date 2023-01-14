package com.rogoshum.magickcore.common.magick.ability;

import com.rogoshum.magickcore.api.enums.ApplyType;
import com.rogoshum.magickcore.common.extradata.entity.ElementToolData;
import com.rogoshum.magickcore.common.entity.projectile.PhantomEntity;
import com.rogoshum.magickcore.common.init.ModEntities;
import com.rogoshum.magickcore.common.lib.LibContext;
import com.rogoshum.magickcore.common.magick.MagickReleaseHelper;
import com.rogoshum.magickcore.common.init.ModBuffs;
import com.rogoshum.magickcore.common.init.ModDamages;
import com.rogoshum.magickcore.common.lib.LibBuff;
import com.rogoshum.magickcore.common.lib.LibElements;
import com.rogoshum.magickcore.common.magick.context.MagickContext;
import com.rogoshum.magickcore.common.magick.context.child.PositionContext;
import com.rogoshum.magickcore.common.magick.context.child.SpawnContext;
import com.rogoshum.magickcore.common.extradata.ExtraDataUtil;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.server.ServerWorld;

import java.util.List;

public class StasisAbility{
    public static boolean hitEntity(MagickContext context) {
        if(context.victim == null) return false;
        return ModBuffs.applyBuff(context.victim, LibBuff.SLOW, context.tick, context.force, false);
    }

    public static boolean damageEntity(MagickContext context) {
        if(context.victim == null) return false;
        if(ModBuffs.hasBuff(context.victim, LibBuff.SLOW))
            context.force *= 1.25;

        boolean flag = false;
        if(context.caster != null && context.projectile instanceof ProjectileEntity)
            flag = context.victim.hurt(ModDamages.applyProjectileStasisDamage(context.caster, context.projectile), context.force);
        else if(context.caster != null)
            flag = context.victim.hurt(ModDamages.applyEntityStasisDamage(context.caster), context.force);
        else if(context.projectile != null)
            flag = context.victim.hurt(ModDamages.applyEntityStasisDamage(context.projectile), context.force);
        else
            flag = context.victim.hurt(ModDamages.getStasisDamage(), context.force);
        if(flag)
            ModBuffs.applyBuff(context.victim, LibBuff.FREEZE, context.tick / 8, 0, false);

        return flag;
    }

    public static boolean hitBlock(MagickContext context) {
        if(!context.world.isClientSide && context.containChild(LibContext.POSITION)) {
            PositionContext positionContext = context.getChild(LibContext.POSITION);

            BlockPos pos = new BlockPos(positionContext.pos);
            if (context.world.getBlockState(pos).getBlock().equals(Blocks.WATER.getBlock())) {
                context.world.setBlockAndUpdate(pos, Blocks.ICE.defaultBlockState());
                return true;
            }

            if (context.world.isEmptyBlock(pos.offset(0, 1, 0)) && Blocks.SNOW.defaultBlockState().canSurvive(context.world, pos.offset(0, 1, 0))) {
                context.world.setBlock(pos.offset(0, 1, 0), Blocks.SNOW.defaultBlockState(), 2);
                return true;
            }
        }
        return false;
    }

    public static boolean applyBuff(MagickContext context) {
        if(context.victim == null) return false;
        return ModBuffs.applyBuff(context.victim, LibBuff.STASIS, context.tick * 2, context.force, true);
    }

    public static boolean applyDebuff(MagickContext context) {
        if(context.victim == null) return false;
        if(context.force >= 7)
            return ModBuffs.applyBuff(context.victim, LibBuff.FREEZE, context.tick, context.force, false);
        return ModBuffs.applyBuff(context.victim, LibBuff.SLOW, context.tick, context.force, false);
    }

    public static boolean applyToolElement(MagickContext context) {
        int level = (int) context.force;
        if(!(context.caster instanceof LivingEntity)) return false;
        LivingEntity entity = (LivingEntity) context.caster;

        boolean worked = false;
        List<Entity> list = entity.level.getEntities(entity, entity.getBoundingBox().inflate(level * 2));
        for (Entity entity1 : list) {
            Vector3d dir = entity1.position().add(0, entity1.getBbHeight() * 0.5, 0).subtract(entity.position().add(0, entity.getBbHeight() * 0.5, 0)).normalize();

            if(dir.dot(entity.getLookAngle()) > 0.3 && !MagickReleaseHelper.sameLikeOwner(entity, entity1)) {
                entity1.setDeltaMovement(entity1.getDeltaMovement().scale(Math.pow(0.7, level)));
                worked = true;
            }
        }

        if(worked && entity.tickCount % 20 == 0) {
            ElementToolData tool = ExtraDataUtil.elementToolData(entity);
            if (tool != null) {
                tool.consumeElementOnTool(entity, LibElements.STASIS);
            }
        }
        return true;
    }

    public static boolean superEntity(MagickContext context) {
        if(context.caster == null) return false;
        SpawnContext spawnContext = new SpawnContext();
        spawnContext.entityType = ModEntities.SILENCE_SQUALL.get();
        context.addChild(spawnContext);
        context.applyType(ApplyType.SPAWN_ENTITY);
        return MagickReleaseHelper.releaseMagick(context);
    }

    public static boolean agglomerate(MagickContext context) {
        if(!(context.victim instanceof LivingEntity) && !context.world.isClientSide) {
            Vector3d pos = Vector3d.ZERO;
            if(context.victim != null)
                pos = context.victim.position();
            if(context.containChild(LibContext.POSITION))
                pos = context.<PositionContext>getChild(LibContext.POSITION).pos;

            if(pos.y > 192) {
                ((ServerWorld)context.world).setWeatherParameters(0, 6000, true, false);
            }
        }
        if(!(context.victim instanceof LivingEntity) || context.world.isClientSide) return false;
        PhantomEntity phantom = new PhantomEntity(ModEntities.PHANTOM.get(), context.world);
        phantom.setEntity((LivingEntity) context.victim);
        phantom.setPos(context.victim.getX(), context.victim.getY(), context.victim.getZ());
        phantom.spellContext().tick(context.tick * 2);
        context.world.addFreshEntity(phantom);
        return true;
    }

    public static boolean diffusion(MagickContext context) {
        if(!(context.victim instanceof LivingEntity)) return false;
        return ModBuffs.applyBuff(context.victim, LibBuff.PURE, context.tick * 2, context.force, true);
    }
}
