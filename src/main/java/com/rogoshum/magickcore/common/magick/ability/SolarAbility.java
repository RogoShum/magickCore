package com.rogoshum.magickcore.common.magick.ability;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.api.enums.ApplyType;
import com.rogoshum.magickcore.api.enums.ParticleType;
import com.rogoshum.magickcore.common.block.ItemExtractorBlock;
import com.rogoshum.magickcore.common.init.ModElements;
import com.rogoshum.magickcore.api.magick.context.MagickContext;
import com.rogoshum.magickcore.common.init.ModBuffs;
import com.rogoshum.magickcore.common.init.ModDamages;
import com.rogoshum.magickcore.common.init.ModEntities;
import com.rogoshum.magickcore.common.lib.LibBuff;
import com.rogoshum.magickcore.common.lib.LibContext;
import com.rogoshum.magickcore.api.magick.MagickReleaseHelper;
import com.rogoshum.magickcore.api.magick.context.child.PositionContext;
import com.rogoshum.magickcore.api.magick.context.child.SpawnContext;
import com.rogoshum.magickcore.common.util.ItemStackUtil;
import com.rogoshum.magickcore.common.util.ParticleUtil;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Explosion;
import net.minecraft.server.level.ServerLevel;

import java.util.Optional;

public class SolarAbility{
    public static boolean radiance(MagickContext context) {
        if(context.victim == null) return false;
        BlockPos pos = context.victim.getOnPos();
        BlockEntity tile = context.world.getBlockEntity(pos);
        ItemStack block = new ItemStack(context.world.getBlockState(pos).getBlock());
        Optional<SmeltingRecipe> optional = context.world.getRecipeManager().getRecipeFor(RecipeType.SMELTING, new SimpleContainer(block), context.world);
        if (optional.isPresent()) {
            ItemStack result = optional.get().getResultItem();
            if(result.getItem() instanceof BlockItem) {
                ParticleUtil.spawnBlastParticle(context.world, context.victim.position(), 2, ModElements.SOLAR, ParticleType.PARTICLE);
                context.world.setBlockAndUpdate(pos, ((BlockItem)result.getItem()).getBlock().defaultBlockState());
                return true;
            }
        }
        if(tile instanceof Container inventory) {
            for(int i = 0; i < inventory.getContainerSize(); ++i) {
                ItemStack stack = inventory.getItem(i);
                optional = context.world.getRecipeManager().getRecipeFor(RecipeType.SMELTING, new SimpleContainer(stack), context.world);
                if (optional.isPresent()) {
                    ItemStack result = optional.get().getResultItem();
                    if(!result.isEmpty()) {
                        stack.shrink(1);
                        ItemStackUtil.dropItem(context.world, result.copy(), pos.above());
                        ParticleUtil.spawnBlastParticle(context.world, context.victim.position(), 2, ModElements.SOLAR, ParticleType.PARTICLE);
                        return true;
                    }
                }
            }
        }
        return false;
    }
    public static boolean hitEntity(MagickContext context) {
        if(context.victim == null) return false;
        if(context.victim instanceof ItemEntity) {
            ItemStack stack = ((ItemEntity) context.victim).getItem();
            Optional<SmeltingRecipe> optional = context.world.getRecipeManager().getRecipeFor(RecipeType.SMELTING, new SimpleContainer(stack), context.world);
            if (optional.isPresent()) {
                ItemStack itemstack = optional.get().getResultItem();
                if (!itemstack.isEmpty()) {
                    ItemStack itemstack1 = itemstack.copy();
                    itemstack1.setCount(stack.getCount() * itemstack.getCount()); //Forge: Support smelting returning multiple
                    if(context.force >= 7) {
                        ItemStack outputCopy = itemstack1.copy();
                        itemstack1 = ItemStackUtil.mergeStacks(itemstack1, outputCopy, 64);
                        if(!outputCopy.isEmpty()) {
                            ItemEntity entity = new ItemEntity(context.victim.level, context.victim.getX(), context.victim.getY(), context.victim.getZ(), outputCopy);
                            if(!entity.level.isClientSide)
                                entity.level.addFreshEntity(entity);
                        }
                    }
                    ((ItemEntity) context.victim).setItem(itemstack1);
                    ParticleUtil.spawnBlastParticle(context.world, context.victim.position().add(0, context.victim.getBbHeight(), 0), 2, ModElements.SOLAR, ParticleType.PARTICLE);
                    return true;
                }
            }
            return false;
        } else
            context.victim.setSecondsOnFire(Math.max(context.tick / 10, 20));
        return context.victim.getRemainingFireTicks() > 0;
    }

    public static boolean damageEntity(MagickContext context) {
        if(context.victim == null || context.victim instanceof ItemEntity) return false;
        if(context.victim.getRemainingFireTicks() > 0)
            context.force *= 1.5;

        if(context.caster != null && context.projectile instanceof Projectile)
            return context.victim.hurt(ModDamages.applyProjectileSolarDamage(context.caster, context.projectile), context.force);
        else if(context.caster != null)
            return context.victim.hurt(ModDamages.applyEntitySolarDamage(context.caster), context.force);
        else if(context.projectile != null)
            return context.victim.hurt(ModDamages.applyEntitySolarDamage(context.projectile), context.force);
        else
            return context.victim.hurt(ModDamages.getSolarDamage(), context.force);
    }

    public static boolean hitBlock(MagickContext context) {
        if(!context.world.isClientSide && context.containChild(LibContext.POSITION)) {
            PositionContext positionContext = context.getChild(LibContext.POSITION);

            BlockPos pos = new BlockPos(positionContext.pos);
            boolean success = false;
            if (context.world.getBlockState(pos).getBlock().equals(Blocks.ICE) || context.world.getBlockState(pos).getBlock().equals(Blocks.SNOW) || context.world.getBlockState(pos).getBlock().equals(Blocks.SNOW_BLOCK)) {
                context.world.setBlockAndUpdate(pos, Blocks.WATER.defaultBlockState());
                success = true;
            }

            if (!success && context.world.isEmptyBlock(pos.offset(0, 1, 0)) && Blocks.FIRE.defaultBlockState().canSurvive(context.world, pos.offset(0, 1, 0))) {
                context.world.setBlockAndUpdate(pos.offset(0, 1, 0), Blocks.FIRE.defaultBlockState());
                success = true;
            }

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
        if(!context.victim.fireImmune()){
            context.victim.setSecondsOnFire((int) (context.tick * (context.force + 1)));
            return context.victim.getRemainingFireTicks() > 0;
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
        if(context.doBlock) {
            PositionContext positionContext = context.getChild(LibContext.POSITION);
            if(positionContext == null)
                return false;
            BlockPos pos = new BlockPos(positionContext.pos);
            Explosion explosion = context.world.explode(context.caster, pos.getX(), pos.getY(), pos.getZ(), context.force, Explosion.BlockInteraction.NONE);
            boolean success = !explosion.getToBlow().isEmpty();
            if(success) {
                ParticleUtil.spawnBlastParticle(context.world, positionContext.pos, context.force, ModElements.SOLAR, ParticleType.MIST);
                return true;
            } else
                return false;
        }
        if(context.victim == null) return false;
        Explosion explosion = context.world.explode(context.caster, context.victim.getX(), context.victim.getY(), context.victim.getZ(), context.force, Explosion.BlockInteraction.NONE);
        boolean success = !explosion.getToBlow().isEmpty();
        if(success)
            ParticleUtil.spawnBlastParticle(context.world, context.victim.position().add(0, context.victim.getBbHeight() * 0.5, 0), context.force, ModElements.SOLAR, ParticleType.MIST);
        return success;
    }

    public static boolean agglomerate(MagickContext context) {
        if(!(context.victim instanceof LivingEntity)) {
            if(!context.world.isClientSide) {
                Vec3 pos = Vec3.ZERO;
                if(context.victim != null)
                    pos = context.victim.position();
                if(context.containChild(LibContext.POSITION))
                    pos = context.<PositionContext>getChild(LibContext.POSITION).pos;

                if(pos.y > 192) {
                    ((ServerLevel)context.world).setWeatherParameters(6000, 0, false, false);
                }
            }
            return false;
        }

        LivingEntity living = ((LivingEntity) context.victim);
        living.hurtTime = 0;
        living.heal(context.force*0.75f);
        if(living.getHealth() == living.getMaxHealth() && living.getAbsorptionAmount() < living.getMaxHealth()) {
            living.setAbsorptionAmount(Math.min(living.getAbsorptionAmount() + living.getMaxHealth() * 0.02f * context.force, living.getMaxHealth()));
        }

        if(!(living instanceof Player)) {
            ParticleOptions iparticledata = ParticleTypes.HEART;

            for(int i = 0; i < 7; ++i) {
                double d0 = MagickCore.rand.nextGaussian() * 0.02D;
                double d1 = MagickCore.rand.nextGaussian() * 0.02D;
                double d2 = MagickCore.rand.nextGaussian() * 0.02D;
                context.victim.level.addParticle(iparticledata, context.victim.getRandomX(1.0D), context.victim.getRandomY() + 0.5D, context.victim.getRandomZ(1.0D), d0, d1, d2);
            }
        }
        return true;
    }
}
