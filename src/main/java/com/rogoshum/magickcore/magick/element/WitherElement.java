package com.rogoshum.magickcore.magick.element;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.init.ModBuff;
import com.rogoshum.magickcore.init.ModDamage;
import com.rogoshum.magickcore.lib.LibBuff;
import com.rogoshum.magickcore.lib.LibElements;
import com.rogoshum.magickcore.magick.ReleaseAttribute;
import com.rogoshum.magickcore.tool.NBTTagHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class WitherElement extends MagickElement{
    public WitherElement(String name, ElementAbility ability) {
        super(name, ability);
    }

    public static class WitherAbility extends ElementAbility{

        public WitherAbility(DamageSource damage) {
            super(damage);
        }

        @Override
        public boolean hitEntity(ReleaseAttribute attribute) {
            return ModBuff.applyBuff(attribute.victim, LibBuff.WITHER, attribute.tick, attribute.force, false);
        }

        @Override
        public boolean damageEntity(ReleaseAttribute attribute) {
            if(ModBuff.hasBuff(attribute.victim, LibBuff.WITHER))
                attribute.force *= 1.25;

            boolean flag = false;
            if(attribute.entity != null && attribute.projectile != null)
                flag = attribute.victim.attackEntityFrom(ModDamage.applyProjectileWitherDamage(attribute.entity, attribute.projectile), attribute.force);
            else if(attribute.entity != null)
                flag = attribute.victim.attackEntityFrom(ModDamage.applyEntityWitherDamage(attribute.entity), attribute.force);
            else if(attribute.projectile != null)
                flag = attribute.victim.attackEntityFrom(ModDamage.applyEntityWitherDamage(attribute.projectile), attribute.force);
            else
                flag = attribute.victim.attackEntityFrom(ModDamage.getWitherDamage(), attribute.force);

            return flag;
        }

        @Override
        public boolean hitBlock(World world, BlockPos block, int tick) {
            return false;
        }

        @Override
        public boolean applyBuff(ReleaseAttribute attribute) {
            return ModBuff.applyBuff(attribute.victim, LibBuff.DECAY, attribute.tick, attribute.force, true);
        }

        @Override
        public boolean applyDebuff(ReleaseAttribute attribute) {
            return ModBuff.applyBuff(attribute.victim, LibBuff.CRIPPLE, attribute.tick, attribute.force, false) && ModBuff.applyBuff(attribute.victim, LibBuff.WITHER, attribute.tick, attribute.force, false);
        }

        @Override
        public void applyToolElement(LivingEntity entity, int level) {}

        @Override
        public void applyToolElement(ItemStack stack, int level) {
            if(stack.getDamage() > 0 && MagickCore.rand.nextInt(100) == 0) {
                stack.setDamage(stack.getDamage() - 1);
                NBTTagHelper.consumeElementOnTool(stack, LibElements.WITHER);
            }
        }
    }
}
