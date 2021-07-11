package com.rogoshum.magickcore.magick.element;

import com.google.common.collect.ImmutableMultimap;
import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.capability.IElementOnTool;
import com.rogoshum.magickcore.helper.NBTTagHelper;
import com.rogoshum.magickcore.init.ModBuff;
import com.rogoshum.magickcore.init.ModDamage;
import com.rogoshum.magickcore.lib.LibBuff;
import com.rogoshum.magickcore.lib.LibElements;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import java.util.Iterator;
import java.util.UUID;

public class VoidElement extends MagickElement{
    public VoidElement(String name, ElementAbility ability) {
        super(name, ability);
    }

    public static class VoidAbility extends ElementAbility{

        public VoidAbility(DamageSource damage) {
            super(damage);
        }

        @Override
        public boolean hitEntity(Entity entity, Entity victim, int tick, float force) {
            return ModBuff.applyBuff(victim, LibBuff.WEAKEN, tick, force, false);
        }

        @Override
        public boolean damageEntity(Entity entity, Entity projectile, Entity victim, int tick, float force) {
            if(ModBuff.hasBuff(victim, LibBuff.WEAKEN))
                force *= 2;

            boolean flag = false;
            if(entity != null && projectile != null)
                flag = victim.attackEntityFrom(ModDamage.applyProjectileVoidDamage(entity, projectile), force);
            else if(entity != null)
                flag = victim.attackEntityFrom(ModDamage.applyEntityVoidDamage(entity), force);
            else if(projectile != null)
                flag = victim.attackEntityFrom(ModDamage.applyEntityVoidDamage(projectile), force);
            else
                flag = victim.attackEntityFrom(ModDamage.getVoidDamage(), force);
            if(flag)
                ModBuff.applyBuff(victim, LibBuff.FRAGILE, 10, 0, false);

            return flag;
        }

        @Override
        public boolean hitBlock(World world, BlockPos pos, int tick) {
            return false;
        }

        @Override
        public boolean applyBuff(Entity victim, int tick, float force) {
            return ModBuff.applyBuff(victim, LibBuff.LIGHT, tick, force, true);
        }

        @Override
        public boolean applyDebuff(Entity victim, int tick, float force) {
            return ModBuff.applyBuff(victim, LibBuff.FRAGILE, tick, force, false);
        }

        @Override
        public void applyToolElement(LivingEntity entity, int level) {
            ItemStack stack = entity.getHeldItemMainhand();
            if(NBTTagHelper.hasElementOnTool(stack, LibElements.VOID))
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
