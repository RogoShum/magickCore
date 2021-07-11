package com.rogoshum.magickcore.api;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IManaAbility {
    public boolean hitEntity(Entity entity, Entity victim, int tick, float force);
    public boolean damageEntity(Entity entity, Entity projectile, Entity victim ,int tick, float force);

    public boolean hitBlock(World world, BlockPos block, int tick);

    public boolean applyBuff(Entity victim, int tick, float force);

    public boolean applyDebuff(Entity victim, int tick, float force);

    public DamageSource getDamageSource();

    public void applyToolElement(LivingEntity entity, int level);

    public void applyToolElement(ItemStack stack, int level);
}
