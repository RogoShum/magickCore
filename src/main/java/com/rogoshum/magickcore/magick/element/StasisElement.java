package com.rogoshum.magickcore.magick.element;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.capability.IElementOnTool;
import com.rogoshum.magickcore.helper.MagickReleaseHelper;
import com.rogoshum.magickcore.init.ModBuff;
import com.rogoshum.magickcore.init.ModDamage;
import com.rogoshum.magickcore.lib.LibBuff;
import com.rogoshum.magickcore.lib.LibElementTool;
import com.rogoshum.magickcore.lib.LibElements;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

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
        public boolean hitEntity(Entity entity, Entity victim, int tick, float force) {
            return ModBuff.applyBuff(victim, LibBuff.SLOW, tick, force, false);
        }

        @Override
        public boolean damageEntity(Entity entity, Entity projectile, Entity victim, int tick, float force) {
            if(ModBuff.hasBuff(victim, LibBuff.SLOW))
                force *= 2;

            boolean flag = false;
            if(entity != null && projectile != null)
                flag = victim.attackEntityFrom(ModDamage.applyProjectileStasisDamage(entity, projectile), force);
            else if(entity != null)
                flag = victim.attackEntityFrom(ModDamage.applyEntityStasisDamage(entity), force);
            else if(projectile != null)
                flag = victim.attackEntityFrom(ModDamage.applyEntityStasisDamage(projectile), force);
            else
                flag = victim.attackEntityFrom(ModDamage.getStasisDamage(), force);
            if(flag)
                ModBuff.applyBuff(victim, LibBuff.FREEZE, tick / 10, 0, false);

            return flag;
        }

        @Override
        public boolean hitBlock(World world, BlockPos pos, int tick) {
            if(world.getBlockState(pos).getBlock().equals(Blocks.WATER.getBlock()))
                world.setBlockState(pos, Blocks.ICE.getDefaultState());

            if(world.isAirBlock(pos.add(0, 1, 0)) && Blocks.SNOW.getDefaultState().isValidPosition(world, pos.add(0, 1, 0)))
                world.setBlockState(pos.add(0, 1, 0), Blocks.SNOW.getDefaultState(), 2);
            return false;
        }

        @Override
        public boolean applyBuff(Entity victim, int tick, float force) {
            return ModBuff.applyBuff(victim, LibBuff.STASIS, tick, force, true);
        }

        @Override
        public boolean applyDebuff(Entity victim, int tick, float force) {
            return ModBuff.applyBuff(victim, LibBuff.FREEZE, tick, force, false);
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
