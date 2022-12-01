package com.rogoshum.magickcore.common.magick.ability;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.api.enums.ApplyType;
import com.rogoshum.magickcore.client.particle.LitParticle;
import com.rogoshum.magickcore.common.magick.context.MagickContext;
import com.rogoshum.magickcore.common.init.ModBuffs;
import com.rogoshum.magickcore.common.init.ModDamages;
import com.rogoshum.magickcore.common.init.ModEntities;
import com.rogoshum.magickcore.common.lib.LibBuff;
import com.rogoshum.magickcore.common.lib.LibContext;
import com.rogoshum.magickcore.common.lib.LibElements;
import com.rogoshum.magickcore.common.magick.MagickReleaseHelper;
import com.rogoshum.magickcore.common.magick.context.child.DirectionContext;
import com.rogoshum.magickcore.common.magick.context.child.PositionContext;
import com.rogoshum.magickcore.common.magick.context.child.SpawnContext;
import net.minecraft.block.Blocks;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;

import java.util.Optional;

public class SolarAbility{
    public static boolean hitEntity(MagickContext context) {
        if(context.victim == null) return false;
        if(context.victim instanceof ItemEntity) {
            ItemStack stack = ((ItemEntity) context.victim).getItem();
            Optional<FurnaceRecipe> optional = context.world.getRecipeManager().getRecipe(IRecipeType.SMELTING, new Inventory(stack), context.world);
            if (optional.isPresent()) {
                ItemStack itemstack = optional.get().getRecipeOutput();
                if (!itemstack.isEmpty()) {
                    ItemStack itemstack1 = itemstack.copy();
                    itemstack1.setCount(stack.getCount() * itemstack.getCount()); //Forge: Support smelting returning multiple
                    ((ItemEntity) context.victim).setItem(itemstack1);
                    return true;
                }
            }
            return false;
        } else
            context.victim.setFire(Math.max(context.tick / 10, 20));
        return context.victim.getFireTimer() > 0;
    }

    public static boolean damageEntity(MagickContext context) {
        if(context.victim == null || context.victim instanceof ItemEntity) return false;
        if(context.victim.getFireTimer() > 0)
            context.force *= 2;

        if(context.caster != null && context.projectile != null)
            return context.victim.attackEntityFrom(ModDamages.applyProjectileSolarDamage(context.caster, context.projectile), context.force);
        else if(context.caster != null)
            return context.victim.attackEntityFrom(ModDamages.applyEntitySolarDamage(context.caster), context.force);
        else if(context.projectile != null)
            return context.victim.attackEntityFrom(ModDamages.applyEntitySolarDamage(context.projectile), context.force);
        else
            return context.victim.attackEntityFrom(ModDamages.getSolarDamage(), context.force);
    }

    public static boolean hitBlock(MagickContext context) {
        if(!context.world.isRemote && context.containChild(LibContext.POSITION)) {
            PositionContext positionContext = context.getChild(LibContext.POSITION);

            BlockPos pos = new BlockPos(positionContext.pos);
            if (context.world.getBlockState(pos).getBlock().equals(Blocks.ICE.getBlock()) || context.world.getBlockState(pos).getBlock().equals(Blocks.SNOW.getBlock()) || context.world.getBlockState(pos).getBlock().equals(Blocks.SNOW_BLOCK.getBlock())) {
                context.world.setBlockState(pos, Blocks.WATER.getDefaultState());
                return true;
            }

            if (context.world.isAirBlock(pos.add(0, 1, 0)) && Blocks.FIRE.getDefaultState().isValidPosition(context.world, pos.add(0, 1, 0))) {
                context.world.setBlockState(pos.add(0, 1, 0), Blocks.FIRE.getDefaultState());
                return true;
            }
        }
        return false;
    }

    public static boolean applyBuff(MagickContext context) {
        if(context.victim == null) return false;
        return ModBuffs.applyBuff(context.victim, LibBuff.RADIANCE_WELL, context.tick, context.force, true);
    }

    public static boolean applyDebuff(MagickContext context) {
        if(context.victim == null) return false;
        if(!context.victim.isImmuneToFire()){
            context.victim.setFire((int) (context.tick * (context.force + 1)));
            return context.victim.getFireTimer() > 0;
        }
        return false;
    }

    public static boolean superEntity(MagickContext context) {
        if(context.caster == null) return false;
        PositionContext positionContext = PositionContext.create(context.caster.getPositionVec());
        context.addChild(positionContext);
        SpawnContext spawnContext = new SpawnContext();
        spawnContext.entityType = ModEntities.RADIANCE_WALL.get();
        context.addChild(spawnContext);
        context.applyType(ApplyType.SPAWN_ENTITY);
        return MagickReleaseHelper.releaseMagick(context);
    }

    public static boolean diffusion(MagickContext context) {
        if(!(context.victim instanceof LivingEntity) || !(context.caster instanceof LivingEntity)) return false;
        Vector3d motion = Vector3d.ZERO;
        if(context.containChild(LibContext.DIRECTION)) {
            motion = context.<DirectionContext>getChild(LibContext.DIRECTION).direction.normalize();
        } else if(context.projectile != null) {
            motion = context.victim.getPositionVec().add(0, context.victim.getHeight() * 0.5, 0).subtract(context.projectile.getPositionVec().add(0, context.projectile.getHeight() * 0.5, 0)).normalize();
        } else if(context.victim == context.caster)
            motion = context.caster.getLookVec().normalize();
        else
            motion = context.victim.getPositionVec().add(0, context.victim.getHeight() * 0.5, 0).subtract(context.caster.getPositionVec().add(0, context.caster.getHeight() * 0.5, 0)).normalize();

        motion = motion.scale(context.force * 0.5);
        context.victim.addVelocity(motion.x, motion.y, motion.z);
        if(context.world.isRemote) {
            Vector3d center = context.victim.getPositionVec().add(0, context.victim.getHeight() * 0.5, 0);
            float count = (10 * context.force);
            motion = motion.scale(0.3);
            for (int i = 0; i < count; ++i) {
                double randX = MagickCore.getNegativeToOne() * 0.01;
                double randY = MagickCore.getNegativeToOne() * 0.01;
                double randZ = MagickCore.getNegativeToOne() * 0.01;
                LitParticle par = new LitParticle(context.world, MagickCore.proxy.getElementRender(LibElements.SOLAR).getParticleTexture()
                        , new Vector3d(center.x, center.y, center.z), 0.1f, 0.1f, 1.0f, 30, MagickCore.proxy.getElementRender(LibElements.SOLAR));
                par.setParticleGravity(0);
                par.setLimitScale();
                par.setGlow();
                par.setCanCollide(false);
                par.addMotion(motion.x + randX * context.force, motion.y + randY * context.force, motion.z + randZ * context.force);
                MagickCore.addMagickParticle(par);
            }
        }

        return true;
    }

    public static boolean agglomerate(MagickContext context) {
        if(!(context.victim instanceof LivingEntity)) return false;
        ((LivingEntity) context.victim).heal(context.force);
        IParticleData iparticledata = ParticleTypes.HEART;

        for(int i = 0; i < 7; ++i) {
            double d0 = MagickCore.rand.nextGaussian() * 0.02D;
            double d1 = MagickCore.rand.nextGaussian() * 0.02D;
            double d2 = MagickCore.rand.nextGaussian() * 0.02D;
            context.victim.world.addParticle(iparticledata, context.victim.getPosXRandom(1.0D), context.victim.getPosYRandom() + 0.5D, context.victim.getPosZRandom(1.0D), d0, d1, d2);
        }
        return true;
    }
}
