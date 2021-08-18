package com.rogoshum.magickcore.api;

import com.rogoshum.magickcore.magick.ReleaseAttribute;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IManaAbility {
    public boolean hitEntity(ReleaseAttribute attribute);
    public boolean damageEntity(ReleaseAttribute attribute);

    public boolean hitBlock(World world, BlockPos block, int tick);

    public boolean applyBuff(ReleaseAttribute attribute);

    public boolean applyDebuff(ReleaseAttribute attribute);

    public DamageSource getDamageSource();

    public void applyToolElement(LivingEntity entity, int level);

    public void applyToolElement(ItemStack stack, int level);
}
