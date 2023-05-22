package com.rogoshum.magickcore.common.magick.ability;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.api.enums.ApplyType;
import com.rogoshum.magickcore.api.enums.ParticleType;
import com.rogoshum.magickcore.api.render.ElementRenderer;
import com.rogoshum.magickcore.client.particle.LitParticle;
import com.rogoshum.magickcore.api.magick.context.MagickContext;
import com.rogoshum.magickcore.api.magick.context.child.*;
import com.rogoshum.magickcore.common.entity.projectile.ManaStarEntity;
import com.rogoshum.magickcore.common.init.ModElements;
import com.rogoshum.magickcore.common.lib.LibContext;
import com.rogoshum.magickcore.api.registry.MagickRegistry;
import com.rogoshum.magickcore.api.magick.MagickReleaseHelper;
import com.rogoshum.magickcore.common.init.ModBuffs;
import com.rogoshum.magickcore.common.init.ModDamages;
import com.rogoshum.magickcore.common.init.ModEntities;
import com.rogoshum.magickcore.common.lib.LibBuff;
import com.rogoshum.magickcore.common.lib.LibElements;
import com.rogoshum.magickcore.common.util.EnergyUtil;
import com.rogoshum.magickcore.api.extradata.ExtraDataUtil;
import com.rogoshum.magickcore.common.util.ParticleUtil;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;

import java.util.List;

public class ArcAbility{
    public static boolean hitEntity(MagickContext context) {
        if(context.doBlock)
            return charge(context);
        if(context.victim == null) return false;
        return ModBuffs.applyBuff(context.victim, LibBuff.PARALYSIS, context.tick(), context.force(), false);
    }

    public static boolean radiance(MagickContext context) {
        if(context.caster == null || context.victim == null) return false;
        BlockEntity tile = context.world.getBlockEntity(context.victim.getOnPos());
        if(tile != null)
            EnergyUtil.receiveEnergy(tile, (int) (context.force() *20));
        if(context.victim instanceof Projectile) {
            Vec3 vec = context.victim.position().subtract(context.caster.position());
            ParticleUtil.spawnBlastParticle(context.world, context.victim.position().add(0, context.victim.getBbHeight() * 0.5, 0), 2, ModElements.ARC, ParticleType.PARTICLE);
            context.victim.setPos(vec.scale(6).add(context.caster.position()));
            return true;
        }
        return false;
    }

    public static boolean damageEntity(MagickContext context) {
        if(context.doBlock)
            return charge(context);
        if(context.victim == null) return false;
        if(ModBuffs.hasBuff(context.victim, LibBuff.PARALYSIS))
            context.force(context.force() * 1.25f);

        boolean flag;
        if(context.caster != null && context.projectile instanceof Projectile)
            flag = context.victim.hurt(ModDamages.applyProjectileArcDamage(context.caster, context.projectile), context.force());
        else if(context.caster != null)
            flag = context.victim.hurt(ModDamages.applyEntityArcDamage(context.caster), context.force());
        else if(context.projectile != null)
            flag = context.victim.hurt(ModDamages.applyEntityArcDamage(context.projectile), context.force());
        else
            flag = context.victim.hurt(ModDamages.getArcDamage(), context.force());

        //if(flag && !victim.world.isRemote)
        //victim.thunderHit((ServerWorld) victim.world, null);

        if(flag) {
            List<Entity> list = context.victim.level.getEntities(context.victim, context.victim.getBoundingBox().inflate(context.range()));

            for(Entity entity1 : list) {
                if(entity1 instanceof LivingEntity && !ModBuffs.hasBuff(entity1, LibBuff.PARALYSIS) &&
                        !MagickReleaseHelper.sameLikeOwner(context.caster, entity1)) {
                    if(!context.victim.level.isClientSide) {
                        ManaStarEntity starEntity = new ManaStarEntity(ModEntities.MANA_STAR.get(), context.victim.level);

                        starEntity.setCaster(context.caster);
                        starEntity.setPos(context.victim.getX(), context.victim.getY() + context.victim.getBbHeight() / 2, context.victim.getZ());
                        Vec3 motion = entity1.position().add(0, entity1.getBbHeight() / 2, 0).subtract(starEntity.position()).normalize();
                        starEntity.shoot(motion.x, motion.y, motion.z, 1.0f, 1.0f);
                        starEntity.spellContext().element(MagickRegistry.getElement(LibElements.ARC));
                        starEntity.spellContext().force(context.force() * 0.5f);
                        starEntity.spellContext().applyType(ApplyType.ATTACK);
                        starEntity.spellContext().tick(Math.max(context.tick() / 10, 20));
                        starEntity.spellContext().range(0);
                        starEntity.spellContext().addChild(TraceContext.create(entity1.getUUID()));
                        context.victim.level.addFreshEntity(starEntity);
                    }
                }
            }
        }

        return flag;
    }

    public static void makeParticle(Level world, Vec3 pos, Vec3 pos1, String type, float scaleP) {
        if(!world.isClientSide) return;
        ElementRenderer renderer = MagickCore.proxy.getElementRender(type);
        double dis = pos.subtract(pos1).length();
        int distance = (int) (10 * dis);
        for (int i = 0; i < distance; i++) {
            double trailFactor = i / (distance - 1.0D);
            double tx = pos.x() + (pos1.x() - pos.x()) * trailFactor + world.random.nextGaussian() * 0.5;
            double ty = pos.y() + (pos1.y() - pos.y()) * trailFactor + world.random.nextGaussian() * 0.5;
            double tz = pos.z() + (pos1.z() - pos.z()) * trailFactor + world.random.nextGaussian() * 0.5;
            LitParticle par = new LitParticle(world, renderer.getParticleTexture()
                    , new Vec3(tx, ty, tz), scaleP, scaleP, 1.0f, 15, renderer);
            par.setParticleGravity(0);
            par.setLimitScale();
            par.setGlow();
            MagickCore.addMagickParticle(par);
        }
    }

    public static boolean hitBlock(MagickContext context) {
        if(!context.world.isClientSide && context.containChild(LibContext.POSITION)) {
            PositionContext positionContext = context.getChild(LibContext.POSITION);
            BlockPos pos = new BlockPos(positionContext.pos);
            if(context.world.getBlockEntity(pos) != null) {
                BlockEntity tile = context.world.getBlockEntity(pos);
                boolean extract = false;

                if(context.containChild(LibContext.APPLY_TYPE)) {
                    ExtraApplyTypeContext applyTypeContext = context.getChild(LibContext.APPLY_TYPE);
                    if(applyTypeContext.applyType == ApplyType.DIFFUSION)
                        extract = true;
                }
                int mana = (int) MagickReleaseHelper.singleContextMana(context) * 100;
                if(!extract)
                    EnergyUtil.receiveEnergy(tile, mana);
                else {
                    int get = (int) (EnergyUtil.extractEnergy(tile, mana) * 0.03);
                    if(get > 0 && context.caster != null)
                        ExtraDataUtil.entityStateData(context.caster, (state) -> state.setManaValue(state.getManaValue() + get));
                }
                return true;
            }
        }
        return false;
    }

    public static boolean charge(MagickContext context) {
        if(!context.world.isClientSide && context.containChild(LibContext.POSITION)) {
            PositionContext positionContext = context.getChild(LibContext.POSITION);
            BlockPos pos = new BlockPos(positionContext.pos);
            if(context.world.getBlockEntity(pos) != null) {
                BlockEntity tile = context.world.getBlockEntity(pos);
                int mana = (int) MagickReleaseHelper.singleContextMana(context) * 5;
                EnergyUtil.receiveEnergy(tile, mana);
                return true;
            }
        }
        return false;
    }

    public static boolean applyBuff(MagickContext context) {
        if(context.doBlock)
            return charge(context);
        if(context.force() >= 1 && context.victim instanceof LivingEntity) {
            boolean flag = ((LivingEntity)context.victim).addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, context.tick() * 2, (int) (context.force() - 1)));
            if(((LivingEntity)context.victim).addEffect(new MobEffectInstance(MobEffects.DIG_SPEED, context.tick() * 2, (int) (context.force() - 1))))
                flag = true;
            return flag;
        }
        return false;
    }

    public static boolean applyDebuff(MagickContext context) {
        if(context.doBlock)
            return charge(context);
        if(context.victim == null) return false;
        return ModBuffs.applyBuff(context.victim, LibBuff.PARALYSIS, context.tick(), context.force(), false);
    }

    public static boolean superEntity(MagickContext context) {
        if(context.caster == null) return false;
        SpawnContext spawnContext = new SpawnContext();
        spawnContext.entityType = ModEntities.CHAOS_REACH.get();
        context.addChild(spawnContext);
        context.applyType(ApplyType.SPAWN_ENTITY);
        return MagickReleaseHelper.releaseMagick(context);
    }

    public static boolean diffusion(MagickContext context) {
        if(context.doBlock) {
            if(!context.world.isClientSide && context.containChild(LibContext.POSITION)) {
                PositionContext positionContext = context.getChild(LibContext.POSITION);
                BlockPos pos = new BlockPos(positionContext.pos);
                if(context.world.getBlockEntity(pos) != null) {
                    BlockEntity tile = context.world.getBlockEntity(pos);
                    int mana = (int) MagickReleaseHelper.singleContextMana(context) * 50;
                    int get = (int) (EnergyUtil.extractEnergy(tile, mana) * 0.03);
                    if(get > 0 && context.caster != null)
                        ExtraDataUtil.entityStateData(context.caster, (state) -> state.setManaValue(state.getManaValue() + get));
                    return true;
                }
            }
        }

        if(!(context.victim instanceof LivingEntity) || !(context.caster instanceof LivingEntity)) return false;
        float health = context.force() * 0.5f;
        if(context.victim.hurt(ModDamages.getArcDamage(), health)) {
            ExtraDataUtil.entityStateData(context.caster, (state) -> state.setManaValue(state.getManaValue() + health * 50));
            return true;
        }

        return false;
    }

    public static boolean agglomerate(MagickContext context) {
        if(context.doBlock)
            return charge(context);
        if(!context.world.isClientSide) {
            Vec3 pos = Vec3.ZERO;
            if(context.victim != null)
                pos = context.victim.position();
            if(context.containChild(LibContext.POSITION))
                pos = context.<PositionContext>getChild(LibContext.POSITION).pos;

            if(pos.y > 192) {
                ((ServerLevel)context.world).setWeatherParameters(0, 6000, true, true);
            }
        }
        if(context.victim == null) {
            return false;
        }
        Vec3 motion = Vec3.ZERO;
        if(context.containChild(LibContext.DIRECTION)) {
            motion = context.<DirectionContext>getChild(LibContext.DIRECTION).direction.normalize();
        } else if(context.projectile != null) {
            motion = context.victim.position().add(0, context.victim.getBbHeight() * 0.5, 0).subtract(context.projectile.position().add(0, context.projectile.getBbHeight() * 0.5, 0)).normalize();
        } else if(context.victim == context.caster)
            motion = context.caster.getLookAngle().normalize();
        else
            motion = context.victim.position().add(0, context.victim.getBbHeight() * 0.5, 0).subtract(context.caster.position().add(0, context.caster.getBbHeight() * 0.5, 0)).normalize();

        motion = motion.scale(context.force() * 0.6);
        Vec3 originMotion = context.victim.getDeltaMovement();
        context.victim.setDeltaMovement(motion.scale(0.8).add(originMotion.scale(0.2)));
        context.victim.setOnGround(true);
        if(context.victim instanceof LivingEntity) {
            ((LivingEntity) context.victim).addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, 60));
        }
        return true;
    }
}
