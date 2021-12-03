package com.rogoshum.magickcore.magick.element;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.init.ModBuff;
import com.rogoshum.magickcore.init.ModDamage;
import com.rogoshum.magickcore.lib.LibBuff;
import com.rogoshum.magickcore.magick.ReleaseAttribute;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
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
        public boolean hitEntity(ReleaseAttribute attribute) {
            attribute.victim.setFire(Math.max(attribute.tick / 10, 20));
            if(attribute.victim.getFireTimer() > 0)
                return true;
            return false;
        }

        @Override
        public boolean damageEntity(ReleaseAttribute attribute) {
            if(attribute.victim.getFireTimer() > 0)
                attribute.force *= 2;

            if(attribute.entity != null && attribute.projectile != null)
                return attribute.victim.attackEntityFrom(ModDamage.applyProjectileSolarDamage(attribute.entity, attribute.projectile), attribute.force);
            else if(attribute.entity != null)
                return attribute.victim.attackEntityFrom(ModDamage.applyEntitySolarDamage(attribute.entity), attribute.force);
            else if(attribute.projectile != null)
                return attribute.victim.attackEntityFrom(ModDamage.applyEntitySolarDamage(attribute.projectile), attribute.force);
            else
                return attribute.victim.attackEntityFrom(ModDamage.getSolarDamage(), attribute.force);
        }

        @Override
        public boolean hitBlock(World world, BlockPos pos, int tick) {
            if(!world.isRemote) {
                if (world.getBlockState(pos).getBlock().equals(Blocks.ICE.getBlock()) || world.getBlockState(pos).getBlock().equals(Blocks.SNOW.getBlock()) || world.getBlockState(pos).getBlock().equals(Blocks.SNOW_BLOCK.getBlock()))
                    world.setBlockState(pos, Blocks.WATER.getDefaultState());

                if (world.isAirBlock(pos.add(0, 1, 0)) && Blocks.FIRE.getDefaultState().isValidPosition(world, pos.add(0, 1, 0)))
                    world.setBlockState(pos.add(0, 1, 0), Blocks.FIRE.getDefaultState());
            }
            return false;
        }

        @Override
        public boolean applyBuff(ReleaseAttribute attribute) {
            return ModBuff.applyBuff(attribute.victim, LibBuff.RADIANCE_WELL, attribute.tick, attribute.force, true);
        }

        @Override
        public boolean applyDebuff(ReleaseAttribute attribute) {
            if(!attribute.victim.isImmuneToFire()){
                attribute.victim.setFire((int) (attribute.tick * (attribute.force + 1)));
                if(attribute.victim.getFireTimer() > 0)
                    return true;
                else
                    return false;
            }
            return false;
        }

        @Override
        public void applyToolElement(LivingEntity entity, int level) {

        }

        @Override
        public void applyToolElement(ItemStack stack, int level) {

        }
    }
}
