package com.rogoshum.magickcore.magick.element;

import com.rogoshum.magickcore.init.ModDamage;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.IndirectEntityDamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class OriginElement extends MagickElement{
    public OriginElement(String name, ElementAbility ability) {
        super(name, ability);
    }

    public static class OriginAbility extends ElementAbility{

        public OriginAbility(DamageSource damage) {
            super(damage);
        }

        @Override
        public boolean hitEntity(Entity entity, Entity victim, int tick, float force) {
            return false;
        }

        @Override
        public boolean damageEntity(Entity entity, Entity projectile, Entity victim, int tick, float force) {
            if(entity != null && projectile != null)
                return victim.attackEntityFrom(new IndirectEntityDamageSource(DamageSource.MAGIC.getDamageType(), projectile, entity), force);
            else if(entity != null)
                return victim.attackEntityFrom(new EntityDamageSource(DamageSource.MAGIC.getDamageType(), entity), force);
            else if(projectile != null)
                return victim.attackEntityFrom(new EntityDamageSource(DamageSource.MAGIC.getDamageType(), projectile), force);
            else
                return victim.attackEntityFrom(DamageSource.MAGIC, force);
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
    }
}
