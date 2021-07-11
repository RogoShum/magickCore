package com.rogoshum.magickcore.magick.element;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.api.EnumManaType;
import com.rogoshum.magickcore.api.event.EntityEvents;
import com.rogoshum.magickcore.capability.IElementOnTool;
import com.rogoshum.magickcore.capability.IEntityState;
import com.rogoshum.magickcore.capability.IManaData;
import com.rogoshum.magickcore.client.element.ElementRenderer;
import com.rogoshum.magickcore.client.particle.LitParticle;
import com.rogoshum.magickcore.entity.ManaStarEntity;
import com.rogoshum.magickcore.helper.MagickReleaseHelper;
import com.rogoshum.magickcore.helper.NBTTagHelper;
import com.rogoshum.magickcore.init.ModBuff;
import com.rogoshum.magickcore.init.ModDamage;
import com.rogoshum.magickcore.init.ModElements;
import com.rogoshum.magickcore.init.ModEntites;
import com.rogoshum.magickcore.lib.LibBuff;
import com.rogoshum.magickcore.lib.LibElements;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.LightningBoltEntity;
import net.minecraft.entity.passive.PigEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.MinecraftForge;

import java.util.List;
import java.util.Vector;

public class ArcElement extends MagickElement{
    public ArcElement(String name, ElementAbility ability) {
        super(name, ability);
    }

    public static class ArcAbility extends ElementAbility{

        public ArcAbility(DamageSource damage) {
            super(damage);
        }

        @Override
        public boolean hitEntity(Entity entity, Entity victim, int tick, float force) {
            return ModBuff.applyBuff(victim, LibBuff.PARALYSIS, Math.max(tick / 10, 20), force, false);
        }

        @Override
        public boolean damageEntity(Entity entity, Entity projectile, Entity victim, int tick, float force) {
            if(ModBuff.hasBuff(victim, LibBuff.PARALYSIS))
                force *= 1.25f;

            boolean flag;
            if(entity != null && projectile != null)
                flag = victim.attackEntityFrom(ModDamage.applyProjectileArcDamage(entity, projectile), force);
            else if(entity != null)
                flag = victim.attackEntityFrom(ModDamage.applyEntityArcDamage(entity), force);
            else if(projectile != null)
                flag = victim.attackEntityFrom(ModDamage.applyEntityArcDamage(projectile), force);
            else
                flag = victim.attackEntityFrom(ModDamage.getArcDamage(), force);

            if(flag && !victim.world.isRemote)
                victim.func_241841_a((ServerWorld) victim.world, null);

            if(flag)
            {
                List<Entity> list = victim.world.getEntitiesWithinAABBExcludingEntity(victim, victim.getBoundingBox().grow(force));

                for(Entity entity1 : list)
                {
                    if(entity1 instanceof LivingEntity && !ModBuff.hasBuff(entity1, LibBuff.PARALYSIS) &&
                            MagickReleaseHelper.sameLikeOwner(victim, entity1)) {
                        if(!victim.world.isRemote) {
                            ManaStarEntity starEntity = new ManaStarEntity(ModEntites.mana_star, victim.world);

                            starEntity.setShooter(entity);
                            starEntity.setPosition(victim.getPosX(), victim.getPosY() + victim.getHeight() / 2, victim.getPosZ());
                            Vector3d motion = entity1.getPositionVec().add(0, entity1.getHeight() / 2, 0).subtract(starEntity.getPositionVec()).normalize();
                            starEntity.shoot(motion.x, motion.y, motion.z, 1.0f, 1.0f);
                            starEntity.setElement(ModElements.getElement(LibElements.ARC));
                            starEntity.setForce(force / 2F);
                            starEntity.setManaType(EnumManaType.ATTACK);
                            starEntity.setTickTime(Math.max(tick / 10, 20));
                            starEntity.setRange(0);
                            starEntity.setTraceTarget(entity1.getUniqueID());
                            victim.world.addEntity(starEntity);
                        }
                    }
                }
            }

            return flag;
        }

        public void makeParticle(World world, Vector3d pos, Vector3d pos1, String type, float scaleP)
        {
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

        @Override
        public boolean hitBlock(World world, BlockPos pos, int tick) {
            return false;
        }

        @Override
        public boolean applyBuff(Entity victim, int tick, float force) {
            if(victim instanceof LivingEntity)
                return ((LivingEntity)victim).addPotionEffect(new EffectInstance(Effects.SPEED, tick, (int) force));
            return false;
        }

        @Override
        public boolean applyDebuff(Entity victim, int tick, float force) {
            return ModBuff.applyBuff(victim, LibBuff.PARALYSIS, tick, force, false);
        }

        @Override
        public void applyToolElement(LivingEntity entity, int level) {
            for(int i = 0; i < level; ++i) {
                entity.tick();
            }

            if(entity.ticksExisted % 20 == 0) {
                IElementOnTool tool = entity.getCapability(MagickCore.elementOnTool).orElse(null);
                if (tool != null) {
                    tool.consumeElementOnTool(entity, LibElements.ARC);
                }
            }
        }

        @Override
        public void applyToolElement(ItemStack stack, int level) {

        }
    }
}
