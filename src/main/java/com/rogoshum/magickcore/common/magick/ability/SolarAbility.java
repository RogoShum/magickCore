package com.rogoshum.magickcore.common.magick.ability;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.api.enums.ApplyType;
import com.rogoshum.magickcore.api.enums.ParticleType;
import com.rogoshum.magickcore.client.particle.LitParticle;
import com.rogoshum.magickcore.common.init.ModElements;
import com.rogoshum.magickcore.common.magick.context.MagickContext;
import com.rogoshum.magickcore.common.init.ModBuffs;
import com.rogoshum.magickcore.common.init.ModDamages;
import com.rogoshum.magickcore.common.init.ModEntities;
import com.rogoshum.magickcore.common.lib.LibBuff;
import com.rogoshum.magickcore.common.lib.LibContext;
import com.rogoshum.magickcore.common.lib.LibElements;
import com.rogoshum.magickcore.common.magick.MagickReleaseHelper;
import com.rogoshum.magickcore.common.magick.context.child.DirectionContext;
import com.rogoshum.magickcore.common.magick.context.child.ExtraApplyTypeContext;
import com.rogoshum.magickcore.common.magick.context.child.PositionContext;
import com.rogoshum.magickcore.common.magick.context.child.SpawnContext;
import com.rogoshum.magickcore.common.util.ItemStackUtil;
import com.rogoshum.magickcore.common.util.ParticleUtil;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.Explosion;
import net.minecraft.world.server.ServerWorld;

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
                    if(context.force >= 7) {
                        ItemStack outputCopy = itemstack1.copy();
                        itemstack1 = ItemStackUtil.mergeStacks(itemstack1, outputCopy, 64);
                        if(!outputCopy.isEmpty()) {
                            ItemEntity entity = new ItemEntity(context.victim.world, context.victim.getPosX(), context.victim.getPosY(), context.victim.getPosZ(), outputCopy);
                            if(!entity.world.isRemote)
                                entity.world.addEntity(entity);
                        }
                    }
                    ((ItemEntity) context.victim).setItem(itemstack1);
                    ParticleUtil.spawnBlastParticle(context.world, context.victim.getPositionVec().add(0, context.victim.getHeight(), 0), 2, ModElements.SOLAR, ParticleType.PARTICLE);
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
            context.force *= 1.5;

        if(context.caster != null && context.projectile instanceof ProjectileEntity)
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
            boolean success = false;
            if (context.world.getBlockState(pos).getBlock().equals(Blocks.ICE.getBlock()) || context.world.getBlockState(pos).getBlock().equals(Blocks.SNOW.getBlock()) || context.world.getBlockState(pos).getBlock().equals(Blocks.SNOW_BLOCK.getBlock())) {
                context.world.setBlockState(pos, Blocks.WATER.getDefaultState());
                success = true;
            }

            if (!success && context.world.isAirBlock(pos.add(0, 1, 0)) && Blocks.FIRE.getDefaultState().isValidPosition(context.world, pos.add(0, 1, 0))) {
                context.world.setBlockState(pos.add(0, 1, 0), Blocks.FIRE.getDefaultState());
                success = true;
            }
            if(!context.containChild(LibContext.APPLY_TYPE)) return success;
            ExtraApplyTypeContext typeContext = context.getChild(LibContext.APPLY_TYPE);
            if(typeContext.applyType != ApplyType.DIFFUSION) return success;
            Explosion explosion = context.world.createExplosion(context.caster, pos.getX(), pos.getY(), pos.getZ(), context.force, Explosion.Mode.NONE);
            success = !explosion.getAffectedBlockPositions().isEmpty();
            if(success)
                ParticleUtil.spawnBlastParticle(context.world, positionContext.pos, context.force, ModElements.SOLAR, ParticleType.MIST);
            return success;
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
        SpawnContext spawnContext = new SpawnContext();
        spawnContext.entityType = ModEntities.RADIANCE_WALL.get();
        context.addChild(spawnContext);
        context.applyType(ApplyType.SPAWN_ENTITY);
        return MagickReleaseHelper.releaseMagick(context);
    }

    public static boolean diffusion(MagickContext context) {
        if(context.victim == null) return false;
        Explosion explosion = context.world.createExplosion(context.caster, context.victim.getPosX(), context.victim.getPosY(), context.victim.getPosZ(), context.force, Explosion.Mode.NONE);
        boolean success = !explosion.getAffectedBlockPositions().isEmpty();
        if(success)
            ParticleUtil.spawnBlastParticle(context.world, context.victim.getPositionVec().add(0, context.victim.getHeight() * 0.5, 0), context.force, ModElements.SOLAR, ParticleType.MIST);
        return success;
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
