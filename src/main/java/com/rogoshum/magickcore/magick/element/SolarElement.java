package com.rogoshum.magickcore.magick.element;

import com.rogoshum.magickcore.init.ModBuff;
import com.rogoshum.magickcore.init.ModDamage;
import com.rogoshum.magickcore.lib.LibBuff;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class SolarElement extends MagickElement{
    public SolarElement(String name, ElementAbility ability) {
        super(name, ability);
    }

    public static class SolarAbility extends ElementAbility{
        public SolarAbility(DamageSource damage) {
            super(damage);
        }

        @Override
        public boolean hitEntity(Entity entity, Entity victim, int tick, float force) {
            victim.setFire((int) (tick + force));
            if(victim.getFireTimer() > 0)
                return true;
            return false;
        }

        @Override
        public boolean damageEntity(Entity entity, Entity projectile, Entity victim, int tick, float force) {
            if(victim.getFireTimer() > 0)
                force *= 2;

            if(entity != null && projectile != null)
                return victim.attackEntityFrom(ModDamage.applyProjectileSolarDamage(entity, projectile), force);
            else if(entity != null)
                return victim.attackEntityFrom(ModDamage.applyEntitySolarDamage(entity), force);
            else if(projectile != null)
                return victim.attackEntityFrom(ModDamage.applyEntitySolarDamage(projectile), force);
            else
                return victim.attackEntityFrom(ModDamage.getSolarDamage(), force);
        }

        @Override
        public boolean hitBlock(World world, BlockPos pos, int tick) {
            if(world.getBlockState(pos).getBlock().equals(Blocks.ICE.getBlock()) || world.getBlockState(pos).getBlock().equals(Blocks.SNOW.getBlock()) || world.getBlockState(pos).getBlock().equals(Blocks.SNOW_BLOCK.getBlock()))
                world.setBlockState(pos, Blocks.WATER.getDefaultState());

            if(world.isAirBlock(pos.add(0, 1, 0)) && Blocks.FIRE.getDefaultState().isValidPosition(world, pos.add(0, 1, 0)))
                world.setBlockState(pos.add(0, 1, 0), Blocks.FIRE.getDefaultState());
            return false;
        }

        @Override
        public boolean applyBuff(Entity entity, int tick, float force) {
            return ModBuff.applyBuff(entity, LibBuff.RADIANCE_WELL, tick, force, true);
        }

        @Override
        public boolean applyDebuff(Entity entity, int tick, float force) {
            entity.setFire((int) (tick + force));
            if(entity.getFireTimer() > 0)
                return true;
            return false;
        }
    }
}
