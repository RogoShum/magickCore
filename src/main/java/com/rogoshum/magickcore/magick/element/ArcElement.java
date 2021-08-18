package com.rogoshum.magickcore.magick.element;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.enums.EnumManaType;
import com.rogoshum.magickcore.capability.IElementOnTool;
import com.rogoshum.magickcore.client.element.ElementRenderer;
import com.rogoshum.magickcore.client.particle.LitParticle;
import com.rogoshum.magickcore.entity.ManaStarEntity;
import com.rogoshum.magickcore.tool.MagickReleaseHelper;
import com.rogoshum.magickcore.init.ModBuff;
import com.rogoshum.magickcore.init.ModDamage;
import com.rogoshum.magickcore.init.ModElements;
import com.rogoshum.magickcore.init.ModEntites;
import com.rogoshum.magickcore.lib.LibBuff;
import com.rogoshum.magickcore.lib.LibElements;
import com.rogoshum.magickcore.magick.ReleaseAttribute;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

import java.util.List;

public class ArcElement extends MagickElement{
    public ArcElement(String name, ElementAbility ability) {
        super(name, ability);
    }

    public static class ArcAbility extends ElementAbility{

        public ArcAbility(DamageSource damage) {
            super(damage);
        }

        @Override
        public boolean hitEntity(ReleaseAttribute attribute) {
            return ModBuff.applyBuff(attribute.victim, LibBuff.PARALYSIS, attribute.tick, attribute.force, false);
        }

        @Override
        public boolean damageEntity(ReleaseAttribute attribute) {
            if(ModBuff.hasBuff(attribute.victim, LibBuff.PARALYSIS))
                attribute.force *= 1.25f;

            boolean flag;
            if(attribute.entity != null && attribute.projectile != null)
                flag = attribute.victim.attackEntityFrom(ModDamage.applyProjectileArcDamage(attribute.entity, attribute.projectile), attribute.force);
            else if(attribute.entity != null)
                flag = attribute.victim.attackEntityFrom(ModDamage.applyEntityArcDamage(attribute.entity), attribute.force);
            else if(attribute.projectile != null)
                flag = attribute.victim.attackEntityFrom(ModDamage.applyEntityArcDamage(attribute.projectile), attribute.force);
            else
                flag = attribute.victim.attackEntityFrom(ModDamage.getArcDamage(), attribute.force);

            //if(flag && !victim.world.isRemote)
                //victim.func_241841_a((ServerWorld) victim.world, null);

            if(flag)
            {
                List<Entity> list = attribute.victim.world.getEntitiesWithinAABBExcludingEntity(attribute.victim, attribute.victim.getBoundingBox().grow(attribute.force));

                for(Entity entity1 : list)
                {
                    if(entity1 instanceof LivingEntity && !ModBuff.hasBuff(entity1, LibBuff.PARALYSIS) &&
                            MagickReleaseHelper.sameLikeOwner(attribute.victim, entity1)) {
                        if(!attribute.victim.world.isRemote) {
                            ManaStarEntity starEntity = new ManaStarEntity(ModEntites.mana_star, attribute.victim.world);

                            starEntity.setShooter(attribute.entity);
                            starEntity.setPosition(attribute.victim.getPosX(), attribute.victim.getPosY() + attribute.victim.getHeight() / 2, attribute.victim.getPosZ());
                            Vector3d motion = entity1.getPositionVec().add(0, entity1.getHeight() / 2, 0).subtract(starEntity.getPositionVec()).normalize();
                            starEntity.shoot(motion.x, motion.y, motion.z, 1.0f, 1.0f);
                            starEntity.setElement(ModElements.getElement(LibElements.ARC));
                            starEntity.setForce(attribute.force / 2F);
                            starEntity.setManaType(EnumManaType.ATTACK);
                            starEntity.setTickTime(Math.max(attribute.tick / 10, 20));
                            starEntity.setRange(0);
                            starEntity.setTraceTarget(entity1.getUniqueID());
                            attribute.victim.world.addEntity(starEntity);
                        }
                    }
                }
            }

            return flag;
        }

        public void makeParticle(World world, Vector3d pos, Vector3d pos1, String type, float scaleP)
        {
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

        @Override
        public boolean hitBlock(World world, BlockPos pos, int tick) {
            return false;
        }

        @Override
        public boolean applyBuff(ReleaseAttribute attribute) {
            if(attribute.victim instanceof LivingEntity)
                return ((LivingEntity)attribute.victim).addPotionEffect(new EffectInstance(Effects.SPEED, attribute.tick, (int) attribute.force));
            return false;
        }

        @Override
        public boolean applyDebuff(ReleaseAttribute attribute) {
            return ModBuff.applyBuff(attribute.victim, LibBuff.PARALYSIS, attribute.tick, attribute.force, false);
        }

        @Override
        public void applyToolElement(LivingEntity entity, int level) {
            for(int i = 0; i < level; ++i) {
                entity.ticksExisted+=MagickCore.rand.nextInt(2) + 1;
                if(entity.isServerWorld())
                    entity.tick();
            }

            entity.addPotionEffect(new EffectInstance(Effects.SPEED, 20, level));

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
