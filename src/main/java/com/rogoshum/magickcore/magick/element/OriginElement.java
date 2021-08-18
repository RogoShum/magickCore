package com.rogoshum.magickcore.magick.element;

import com.rogoshum.magickcore.magick.ReleaseAttribute;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
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
        public boolean hitEntity(ReleaseAttribute attribute) {
            return false;
        }

        @Override
        public boolean damageEntity(ReleaseAttribute attribute) {
            if(attribute.entity != null && attribute.projectile != null)
                return attribute.victim.attackEntityFrom(new IndirectEntityDamageSource(DamageSource.MAGIC.getDamageType(), attribute.projectile, attribute.entity), attribute.force);
            else if(attribute.entity != null)
                return attribute.victim.attackEntityFrom(new EntityDamageSource(DamageSource.MAGIC.getDamageType(), attribute.entity), attribute.force);
            else if(attribute.projectile != null)
                return attribute.victim.attackEntityFrom(new EntityDamageSource(DamageSource.MAGIC.getDamageType(), attribute.projectile), attribute.force);
            else
                return attribute.victim.attackEntityFrom(DamageSource.MAGIC, attribute.force);
        }

        @Override
        public boolean hitBlock(World world, BlockPos pos, int tick) {
            return false;
        }

        @Override
        public boolean applyBuff(ReleaseAttribute attribute) {
            return false;
        }

        @Override
        public boolean applyDebuff(ReleaseAttribute attribute) {
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
