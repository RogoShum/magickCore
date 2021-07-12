package com.rogoshum.magickcore.magick.element;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.helper.NBTTagHelper;
import com.rogoshum.magickcore.init.ModBuff;
import com.rogoshum.magickcore.init.ModDamage;
import com.rogoshum.magickcore.lib.LibBuff;
import com.rogoshum.magickcore.lib.LibElements;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Effect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import java.util.Iterator;
import java.util.Random;

public class WitherElement extends MagickElement{
    public WitherElement(String name, ElementAbility ability) {
        super(name, ability);
    }

    public static class WitherAbility extends ElementAbility{

        public WitherAbility(DamageSource damage) {
            super(damage);
        }

        @Override
        public boolean hitEntity(Entity entity, Entity victim, int tick, float force) {
            return ModBuff.applyBuff(victim, LibBuff.WITHER, tick, force, false);
        }

        @Override
        public boolean damageEntity(Entity entity, Entity projectile, Entity victim, int tick, float force) {
            if(ModBuff.hasBuff(victim, LibBuff.WITHER))
                force *= 1.5;

            boolean flag = false;
            if(entity != null && projectile != null)
                flag = victim.attackEntityFrom(ModDamage.applyProjectileWitherDamage(entity, projectile), force);
            else if(entity != null)
                flag = victim.attackEntityFrom(ModDamage.applyEntityWitherDamage(entity), force);
            else if(projectile != null)
                flag = victim.attackEntityFrom(ModDamage.applyEntityWitherDamage(projectile), force);
            else
                flag = victim.attackEntityFrom(ModDamage.getWitherDamage(), force);

            return flag;
        }

        @Override
        public boolean hitBlock(World world, BlockPos block, int tick) {
            return false;
        }

        @Override
        public boolean applyBuff(Entity victim, int tick, float force) {
            return ModBuff.applyBuff(victim, LibBuff.DECAY, tick, force, true);
        }

        @Override
        public boolean applyDebuff(Entity victim, int tick, float force) {
            return ModBuff.applyBuff(victim, LibBuff.CRIPPLE, tick, force, false);
        }

        @Override
        public void applyToolElement(LivingEntity entity, int level) {}

        @Override
        public void applyToolElement(ItemStack stack, int level) {
            if(stack.getDamage() > 0 && MagickCore.rand.nextInt(200) == 0) {
                stack.setDamage(stack.getDamage() - 1);
                NBTTagHelper.consumeElementOnTool(stack, LibElements.WITHER);
            }
        }
    }
}
