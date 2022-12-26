package com.rogoshum.magickcore.common.magick.ability;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.api.enums.ApplyType;
import com.rogoshum.magickcore.client.element.ElementRenderer;
import com.rogoshum.magickcore.client.particle.LitParticle;
import com.rogoshum.magickcore.common.magick.context.MagickContext;
import com.rogoshum.magickcore.common.magick.context.child.*;
import com.rogoshum.magickcore.common.entity.projectile.ManaStarEntity;
import com.rogoshum.magickcore.common.lib.LibContext;
import com.rogoshum.magickcore.common.registry.MagickRegistry;
import com.rogoshum.magickcore.common.magick.MagickReleaseHelper;
import com.rogoshum.magickcore.common.init.ModBuffs;
import com.rogoshum.magickcore.common.init.ModDamages;
import com.rogoshum.magickcore.common.init.ModEntities;
import com.rogoshum.magickcore.common.lib.LibBuff;
import com.rogoshum.magickcore.common.lib.LibElements;
import com.rogoshum.magickcore.common.util.EnergyUtil;
import com.rogoshum.magickcore.common.extradata.ExtraDataUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import java.util.List;

public class ArcAbility{
    public static boolean hitEntity(MagickContext context) {
        if(context.doBlock)
            return charge(context);
        if(context.victim == null) return false;
        return ModBuffs.applyBuff(context.victim, LibBuff.PARALYSIS, context.tick, context.force, false);
    }

    public static boolean damageEntity(MagickContext context) {
        if(context.doBlock)
            return charge(context);
        if(context.victim == null) return false;
        if(ModBuffs.hasBuff(context.victim, LibBuff.PARALYSIS))
            context.force *= 1.25f;

        boolean flag;
        if(context.caster != null && context.projectile instanceof ProjectileEntity)
            flag = context.victim.attackEntityFrom(ModDamages.applyProjectileArcDamage(context.caster, context.projectile), context.force);
        else if(context.caster != null)
            flag = context.victim.attackEntityFrom(ModDamages.applyEntityArcDamage(context.caster), context.force);
        else if(context.projectile != null)
            flag = context.victim.attackEntityFrom(ModDamages.applyEntityArcDamage(context.projectile), context.force);
        else
            flag = context.victim.attackEntityFrom(ModDamages.getArcDamage(), context.force);

        //if(flag && !victim.world.isRemote)
        //victim.func_241841_a((ServerWorld) victim.world, null);

        if(flag) {
            List<Entity> list = context.victim.world.getEntitiesWithinAABBExcludingEntity(context.victim, context.victim.getBoundingBox().grow(context.range));

            for(Entity entity1 : list) {
                if(entity1 instanceof LivingEntity && !ModBuffs.hasBuff(entity1, LibBuff.PARALYSIS) &&
                        !MagickReleaseHelper.sameLikeOwner(context.caster, entity1)) {
                    if(!context.victim.world.isRemote) {
                        ManaStarEntity starEntity = new ManaStarEntity(ModEntities.MANA_STAR.get(), context.victim.world);

                        starEntity.setShooter(context.caster);
                        starEntity.setPosition(context.victim.getPosX(), context.victim.getPosY() + context.victim.getHeight() / 2, context.victim.getPosZ());
                        Vector3d motion = entity1.getPositionVec().add(0, entity1.getHeight() / 2, 0).subtract(starEntity.getPositionVec()).normalize();
                        starEntity.shoot(motion.x, motion.y, motion.z, 1.0f, 1.0f);
                        starEntity.spellContext().element(MagickRegistry.getElement(LibElements.ARC));
                        starEntity.spellContext().force(context.force * 0.5f);
                        starEntity.spellContext().applyType(ApplyType.ATTACK);
                        starEntity.spellContext().tick(Math.max(context.tick / 10, 20));
                        starEntity.spellContext().range(0);
                        starEntity.spellContext().addChild(TraceContext.create(entity1.getUniqueID()));
                        context.victim.world.addEntity(starEntity);
                    }
                }
            }
        }

        return flag;
    }

    public static void makeParticle(World world, Vector3d pos, Vector3d pos1, String type, float scaleP) {
        if(!world.isRemote) return;
        ElementRenderer renderer = MagickCore.proxy.getElementRender(type);
        double dis = pos.subtract(pos1).length();
        int distance = (int) (10 * dis);
        for (int i = 0; i < distance; i++) {
            double trailFactor = i / (distance - 1.0D);
            double tx = pos.getX() + (pos1.getX() - pos.getX()) * trailFactor + world.rand.nextGaussian() * 0.5;
            double ty = pos.getY() + (pos1.getY() - pos.getY()) * trailFactor + world.rand.nextGaussian() * 0.5;
            double tz = pos.getZ() + (pos1.getZ() - pos.getZ()) * trailFactor + world.rand.nextGaussian() * 0.5;
            LitParticle par = new LitParticle(world, renderer.getParticleTexture()
                    , new Vector3d(tx, ty, tz), scaleP, scaleP, 1.0f, 15, renderer);
            par.setParticleGravity(0);
            par.setLimitScale();
            par.setGlow();
            MagickCore.addMagickParticle(par);
        }
    }

    public static boolean hitBlock(MagickContext context) {
        if(!context.world.isRemote && context.containChild(LibContext.POSITION)) {
            PositionContext positionContext = context.getChild(LibContext.POSITION);
            BlockPos pos = new BlockPos(positionContext.pos);
            if(context.world.getTileEntity(pos) != null) {
                TileEntity tile = context.world.getTileEntity(pos);
                boolean extract = false;

                if(context.containChild(LibContext.APPLY_TYPE)) {
                    ExtraApplyTypeContext applyTypeContext = context.getChild(LibContext.APPLY_TYPE);
                    if(applyTypeContext.applyType == ApplyType.DIFFUSION)
                        extract = true;
                }
                int mana = (int) MagickReleaseHelper.singleContextMana(context) * 25;
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
        if(!context.world.isRemote && context.containChild(LibContext.POSITION)) {
            PositionContext positionContext = context.getChild(LibContext.POSITION);
            BlockPos pos = new BlockPos(positionContext.pos);
            if(context.world.getTileEntity(pos) != null) {
                TileEntity tile = context.world.getTileEntity(pos);
                int mana = (int) MagickReleaseHelper.singleContextMana(context) * 50;
                EnergyUtil.receiveEnergy(tile, mana);
                return true;
            }
        }
        return false;
    }

    public static boolean applyBuff(MagickContext context) {
        if(context.doBlock)
            return charge(context);
        if(context.force >= 1 && context.victim instanceof LivingEntity) {
            boolean flag = ((LivingEntity)context.victim).addPotionEffect(new EffectInstance(Effects.SPEED, context.tick * 2, (int) (context.force - 1)));
            if(((LivingEntity)context.victim).addPotionEffect(new EffectInstance(Effects.HASTE, context.tick * 2, (int) (context.force - 1))))
                flag = true;
            return flag;
        }
        return false;
    }

    public static boolean applyDebuff(MagickContext context) {
        if(context.doBlock)
            return charge(context);
        if(context.victim == null) return false;
        return ModBuffs.applyBuff(context.victim, LibBuff.PARALYSIS, context.tick, context.force, false);
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
            if(!context.world.isRemote && context.containChild(LibContext.POSITION)) {
                PositionContext positionContext = context.getChild(LibContext.POSITION);
                BlockPos pos = new BlockPos(positionContext.pos);
                if(context.world.getTileEntity(pos) != null) {
                    TileEntity tile = context.world.getTileEntity(pos);
                    int mana = (int) MagickReleaseHelper.singleContextMana(context) * 50;
                    int get = (int) (EnergyUtil.extractEnergy(tile, mana) * 0.03);
                    if(get > 0 && context.caster != null)
                        ExtraDataUtil.entityStateData(context.caster, (state) -> state.setManaValue(state.getManaValue() + get));
                    return true;
                }
            }
        }

        if(!(context.victim instanceof LivingEntity) || !(context.caster instanceof LivingEntity)) return false;
        float health = context.force * 0.5f;
        if(context.victim.attackEntityFrom(ModDamages.getArcDamage(), health)) {
            ExtraDataUtil.entityStateData(context.caster, (state) -> state.setManaValue(state.getManaValue() + health * 50));
            return true;
        }

        return false;
    }

    public static boolean agglomerate(MagickContext context) {
        if(context.doBlock)
            return charge(context);
        if(!context.world.isRemote) {
            Vector3d pos = Vector3d.ZERO;
            if(context.victim != null)
                pos = context.victim.getPositionVec();
            if(context.containChild(LibContext.POSITION))
                pos = context.<PositionContext>getChild(LibContext.POSITION).pos;

            if(pos.y > 192) {
                ((ServerWorld)context.world).func_241113_a_(0, 6000, true, true);
            }
        }
        if(context.victim == null) {
            return false;
        }
        Vector3d motion = Vector3d.ZERO;
        if(context.containChild(LibContext.DIRECTION)) {
            motion = context.<DirectionContext>getChild(LibContext.DIRECTION).direction.normalize();
        } else if(context.projectile != null) {
            motion = context.victim.getPositionVec().add(0, context.victim.getHeight() * 0.5, 0).subtract(context.projectile.getPositionVec().add(0, context.projectile.getHeight() * 0.5, 0)).normalize();
        } else if(context.victim == context.caster)
            motion = context.caster.getLookVec().normalize();
        else
            motion = context.victim.getPositionVec().add(0, context.victim.getHeight() * 0.5, 0).subtract(context.caster.getPositionVec().add(0, context.caster.getHeight() * 0.5, 0)).normalize();

        motion = motion.scale(context.force * 0.2);
        Vector3d originMotion = context.victim.getMotion();
        context.victim.setMotion(motion.scale(0.8).add(originMotion.scale(0.2)));
        context.victim.setOnGround(true);
        if(context.victim instanceof LivingEntity) {
            ((LivingEntity) context.victim).addPotionEffect(new EffectInstance(Effects.SLOW_FALLING, 60));
        }
        return true;
    }
}
