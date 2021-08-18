package com.rogoshum.magickcore.magick.element;

import com.rogoshum.magickcore.tool.NBTTagHelper;
import com.rogoshum.magickcore.init.ModBuff;
import com.rogoshum.magickcore.init.ModDamage;
import com.rogoshum.magickcore.lib.LibBuff;
import com.rogoshum.magickcore.lib.LibElements;
import com.rogoshum.magickcore.magick.ReleaseAttribute;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class VoidElement extends MagickElement{
    public VoidElement(String name, ElementAbility ability) {
        super(name, ability);
    }

    public static class VoidAbility extends ElementAbility{

        public VoidAbility(DamageSource damage) {
            super(damage);
        }

        @Override
        public boolean hitEntity(ReleaseAttribute attribute) {
            return ModBuff.applyBuff(attribute.victim, LibBuff.WEAKEN, attribute.tick, attribute.force, false);
        }

        @Override
        public boolean damageEntity(ReleaseAttribute attribute) {
            if(ModBuff.hasBuff(attribute.victim, LibBuff.WEAKEN))
                attribute.force *= 2;

            boolean flag = false;
            if(attribute.entity != null && attribute.projectile != null)
                flag = attribute.victim.attackEntityFrom(ModDamage.applyProjectileVoidDamage(attribute.entity, attribute.projectile), attribute.force);
            else if(attribute.entity != null)
                flag = attribute.victim.attackEntityFrom(ModDamage.applyEntityVoidDamage(attribute.entity), attribute.force);
            else if(attribute.projectile != null)
                flag = attribute.victim.attackEntityFrom(ModDamage.applyEntityVoidDamage(attribute.projectile), attribute.force);
            else
                flag = attribute.victim.attackEntityFrom(ModDamage.getVoidDamage(), attribute.force);
            if(flag)
                ModBuff.applyBuff(attribute.victim, LibBuff.FRAGILE, 10, 0, false);

            return flag;
        }

        @Override
        public boolean hitBlock(World world, BlockPos pos, int tick) {
            return false;
        }

        @Override
        public boolean applyBuff(ReleaseAttribute attribute) {
            return ModBuff.applyBuff(attribute.victim, LibBuff.LIGHT, attribute.tick, attribute.force, true);
        }

        @Override
        public boolean applyDebuff(ReleaseAttribute attribute) {
            return ModBuff.applyBuff(attribute.victim, LibBuff.FRAGILE, attribute.tick, attribute.force, false);
        }

        @Override
        public void applyToolElement(LivingEntity entity, int level) {
            ItemStack stack = entity.getHeldItemMainhand();
            if(stack.hasTag() && NBTTagHelper.hasElementOnTool(stack, LibElements.VOID))
            {
                CompoundNBT tag = NBTTagHelper.getStackTag(stack);
                tag.putInt("VOID_LEVEL", level);
                stack.setTag(tag);
            }
        }

        @Override
        public void applyToolElement(ItemStack stack, int level) {}
    }
}
