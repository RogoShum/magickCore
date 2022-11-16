package com.rogoshum.magickcore.common.magick.ability;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.common.api.enums.ApplyType;
import com.rogoshum.magickcore.client.element.ElementRenderer;
import com.rogoshum.magickcore.client.particle.LitParticle;
import com.rogoshum.magickcore.common.magick.context.MagickContext;
import com.rogoshum.magickcore.common.magick.context.child.*;
import com.rogoshum.magickcore.common.extradata.entity.ElementToolData;
import com.rogoshum.magickcore.common.entity.projectile.ManaStarEntity;
import com.rogoshum.magickcore.common.lib.LibContext;
import com.rogoshum.magickcore.common.lib.LibEntityData;
import com.rogoshum.magickcore.common.registry.MagickRegistry;
import com.rogoshum.magickcore.common.magick.MagickReleaseHelper;
import com.rogoshum.magickcore.common.init.ModBuff;
import com.rogoshum.magickcore.common.init.ModDamage;
import com.rogoshum.magickcore.common.init.ModEntities;
import com.rogoshum.magickcore.common.lib.LibBuff;
import com.rogoshum.magickcore.common.lib.LibElements;
import com.rogoshum.magickcore.common.util.EnergyUtil;
import com.rogoshum.magickcore.common.extradata.ExtraDataUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

import java.util.List;

public class ArcAbility{
    public static boolean hitEntity(MagickContext context) {
        if(context.victim == null) return false;
        return ModBuff.applyBuff(context.victim, LibBuff.PARALYSIS, context.tick, context.force, false);
    }

    public static boolean damageEntity(MagickContext context) {
        if(context.victim == null) return false;
        if(ModBuff.hasBuff(context.victim, LibBuff.PARALYSIS))
            context.force *= 1.25f;

        boolean flag;
        if(context.caster != null && context.projectile != null)
            flag = context.victim.attackEntityFrom(ModDamage.applyProjectileArcDamage(context.caster, context.projectile), context.force);
        else if(context.caster != null)
            flag = context.victim.attackEntityFrom(ModDamage.applyEntityArcDamage(context.caster), context.force);
        else if(context.projectile != null)
            flag = context.victim.attackEntityFrom(ModDamage.applyEntityArcDamage(context.projectile), context.force);
        else
            flag = context.victim.attackEntityFrom(ModDamage.getArcDamage(), context.force);

        //if(flag && !victim.world.isRemote)
        //victim.func_241841_a((ServerWorld) victim.world, null);

        if(flag) {
            List<Entity> list = context.victim.world.getEntitiesWithinAABBExcludingEntity(context.victim, context.victim.getBoundingBox().grow(context.range));

            for(Entity entity1 : list) {
                if(entity1 instanceof LivingEntity && !ModBuff.hasBuff(entity1, LibBuff.PARALYSIS) &&
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
                int mana = (int) MagickReleaseHelper.singleContextMana(context) * 2;
                if(!extract)
                    EnergyUtil.receiveEnergy(tile, mana);
                else {
                    int get = EnergyUtil.extractEnergy(tile, mana);
                    if(get > 0 && context.caster != null)
                        ExtraDataUtil.entityStateData(context.caster, (state) -> state.setManaValue(state.getManaValue() + get));
                }
            }
        }
        return false;
    }

    public static boolean applyBuff(MagickContext context) {
        if(context.victim instanceof LivingEntity)
            return ((LivingEntity)context.victim).addPotionEffect(new EffectInstance(Effects.SPEED, context.tick, (int) context.force));
        return false;
    }

    public static boolean applyDebuff(MagickContext context) {
        if(context.victim == null) return false;
        return ModBuff.applyBuff(context.victim, LibBuff.PARALYSIS, context.tick, context.force, false);
    }

    public static boolean applyToolElement(MagickContext context) {
        int level = (int) context.force;
        if(!(context.caster instanceof LivingEntity)) return false;
        LivingEntity entity = (LivingEntity) context.caster;
        for(int i = 0; i < level; ++i) {
            entity.ticksExisted+=MagickCore.rand.nextInt(2) + 1;
            if(entity.isServerWorld())
                entity.tick();
        }

        //entity.addPotionEffect(new EffectInstance(Effects.SPEED, 20, level));

        if(entity.ticksExisted % 40 * level == 0) {
            ExtraDataUtil.entityData(entity).<ElementToolData>execute(LibEntityData.ELEMENT_TOOL, data -> data.consumeElementOnTool(entity, LibElements.ARC));
        }
        return true;
    }

    public static boolean superEntity(MagickContext context) {
        if(context.caster == null) return false;
        PositionContext positionContext = PositionContext.create(context.caster.getPositionVec());
        positionContext.pos = positionContext.pos.add(0, 1, 0);
        context.addChild(positionContext);
        SpawnContext spawnContext = new SpawnContext();
        spawnContext.entityType = ModEntities.CHAOS_REACH.get();
        context.addChild(spawnContext);
        context.applyType(ApplyType.SPAWN_ENTITY);
        return MagickReleaseHelper.releaseMagick(context);
    }

    public static boolean diffusion(MagickContext context) {
        if(!(context.victim instanceof LivingEntity) || !(context.caster instanceof LivingEntity)) return false;
        float health = context.force * 0.5f;
        if(context.victim.attackEntityFrom(ModDamage.getArcDamage(), health)) {
            ExtraDataUtil.entityStateData(context.caster, (state) -> state.setManaValue(state.getManaValue() + health * 50));
            return true;
        }

        return false;
    }

    public static boolean agglomerate(MagickContext context) {
        if(context.victim == null) return false;
        if(context.containChild(LibContext.DIRECTION)) {
            Vector3d dir = context.<DirectionContext>getChild(LibContext.DIRECTION).direction.normalize().scale(0.2 * context.force);
            Vector3d originMotion = context.victim.getMotion();
            context.victim.setMotion(dir.scale(0.8).add(originMotion.scale(0.2)));
            return true;
        }
        return false;
    }
}
