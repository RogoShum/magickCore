package com.rogoshum.magickcore.magick.element;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.enums.EnumManaLimit;
import com.rogoshum.magickcore.capability.IElementOnTool;
import com.rogoshum.magickcore.tool.MagickReleaseHelper;
import com.rogoshum.magickcore.init.ModBuff;
import com.rogoshum.magickcore.init.ModDamage;
import com.rogoshum.magickcore.lib.LibBuff;
import com.rogoshum.magickcore.lib.LibElements;
import com.rogoshum.magickcore.magick.ReleaseAttribute;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;

public class StasisElement extends MagickElement{
    public StasisElement(String name, ElementAbility ability) {
        super(name, ability);
    }

    public static class StasisAbility extends ElementAbility{

        public StasisAbility(DamageSource damage) {
            super(damage);
        }

        @Override
        public boolean hitEntity(ReleaseAttribute attribute) {
            return ModBuff.applyBuff(attribute.victim, LibBuff.SLOW, attribute.tick, attribute.force, false);
        }

        @Override
        public boolean damageEntity(ReleaseAttribute attribute) {
            if(ModBuff.hasBuff(attribute.victim, LibBuff.SLOW))
                attribute.force *= 1.5;

            boolean flag = false;
            if(attribute.entity != null && attribute.projectile != null)
                flag = attribute.victim.attackEntityFrom(ModDamage.applyProjectileStasisDamage(attribute.entity, attribute.projectile), attribute.force);
            else if(attribute.entity != null)
                flag = attribute.victim.attackEntityFrom(ModDamage.applyEntityStasisDamage(attribute.entity), attribute.force);
            else if(attribute.projectile != null)
                flag = attribute.victim.attackEntityFrom(ModDamage.applyEntityStasisDamage(attribute.projectile), attribute.force);
            else
                flag = attribute.victim.attackEntityFrom(ModDamage.getStasisDamage(), attribute.force);
            if(flag)
                ModBuff.applyBuff(attribute.victim, LibBuff.FREEZE, attribute.tick / 8, 0, false);

            return flag;
        }

        @Override
        public boolean hitBlock(World world, BlockPos pos, int tick) {
            if(!world.isRemote) {
                if (world.getBlockState(pos).getBlock().equals(Blocks.WATER.getBlock()))
                    world.setBlockState(pos, Blocks.ICE.getDefaultState());

                if (world.isAirBlock(pos.add(0, 1, 0)) && Blocks.SNOW.getDefaultState().isValidPosition(world, pos.add(0, 1, 0)))
                    world.setBlockState(pos.add(0, 1, 0), Blocks.SNOW.getDefaultState(), 2);
            }
            return false;
        }

        @Override
        public boolean applyBuff(ReleaseAttribute attribute) {
            return ModBuff.applyBuff(attribute.victim, LibBuff.STASIS, attribute.tick, attribute.force, true);
        }

        @Override
        public boolean applyDebuff(ReleaseAttribute attribute) {
            if(attribute.tick >= EnumManaLimit.TICK.getValue())
                return ModBuff.applyBuff(attribute.victim, LibBuff.FREEZE, attribute.tick, attribute.force, false);
            return ModBuff.applyBuff(attribute.victim, LibBuff.SLOW, attribute.tick, attribute.force, false);
        }

        @Override
        public void applyToolElement(LivingEntity entity, int level) {
            boolean worked = false;
            List<Entity> list = entity.world.getEntitiesWithinAABBExcludingEntity(entity, entity.getBoundingBox().grow(level * 1.5));
            for (Entity entity1 : list)
            {
                if(!MagickReleaseHelper.sameLikeOwner(entity, entity1)) {
                    entity1.setMotion(entity1.getMotion().scale(Math.pow(0.85, level)));
                    worked = true;
                }
            }

            if(worked && entity.ticksExisted % 15 == 0) {
                IElementOnTool tool = entity.getCapability(MagickCore.elementOnTool).orElse(null);
                if (tool != null) {
                    tool.consumeElementOnTool(entity, LibElements.STASIS);
                }
            }
        }

        @Override
        public void applyToolElement(ItemStack stack, int level) {

        }
    }
}
