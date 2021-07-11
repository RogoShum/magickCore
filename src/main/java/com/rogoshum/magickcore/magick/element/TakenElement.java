package com.rogoshum.magickcore.magick.element;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class TakenElement extends MagickElement{
    public TakenElement(String name, ElementAbility ability) {
        super(name, ability);
    }

    public static class TakenAbility extends ElementAbility{

        public TakenAbility(DamageSource damage) {
            super(damage);
        }

        @Override
        public boolean hitEntity(Entity entity, Entity victim, int tick, float force) {
            return false;
        }

        @Override
        public boolean damageEntity(Entity entity, Entity projectile, Entity victim, int tick, float force) {
            return false;
        }

        @Override
        public boolean hitBlock(World world, BlockPos pos, int tick) {
            return false;
        }

        @Override
        public boolean applyBuff(Entity victim, int tick, float force) {
            return false;
        }

        @Override
        public boolean applyDebuff(Entity victim, int tick, float force) {
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
