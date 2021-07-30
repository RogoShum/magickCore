package com.rogoshum.magickcore.magick.element;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.enums.EnumManaLimit;
import com.rogoshum.magickcore.capability.ITakenState;
import com.rogoshum.magickcore.init.ModBuff;
import com.rogoshum.magickcore.init.ModDamage;
import com.rogoshum.magickcore.lib.LibBuff;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class TakenElement extends MagickElement{
    public TakenElement(String name, ElementAbility ability) {
        super(name, ability);
    }

    public static class TakenAbility extends ElementAbility{

        public TakenAbility(DamageSource damage) {
            super(damage);
        }

        @Override
        public boolean hitEntity(Entity entity, Entity victim, int tick, float force) {
            return ModBuff.applyBuff(victim, LibBuff.TAKEN, tick, force, true);
        }

        @Override
        public boolean damageEntity(Entity entity, Entity projectile, Entity victim, int tick, float force) {
            if(ModBuff.hasBuff(victim, LibBuff.TAKEN))
                force *= 1.75;

            boolean flag = false;
            if(entity != null && projectile != null)
                flag = victim.attackEntityFrom(ModDamage.applyProjectileTakenDamage(entity, projectile), force);
            else if(entity != null)
                flag = victim.attackEntityFrom(ModDamage.applyEntityTakenDamage(entity), force);
            else if(projectile != null)
                flag = victim.attackEntityFrom(ModDamage.applyEntityTakenDamage(projectile), force);
            else
                flag = victim.attackEntityFrom(ModDamage.getTakenDamage(), force);

            if(flag && force >= EnumManaLimit.FORCE.getValue() * 1.75 && entity != null && victim instanceof MobEntity && ModBuff.hasBuff(victim, LibBuff.TAKEN))
            {
                ITakenState state = victim.getCapability(MagickCore.takenState).orElse(null);
                state.setOwner(entity.getUniqueID());
                state.setTime(tick);
                victim.playSound(SoundEvents.ENTITY_BLAZE_HURT, 2.0F, 0.0f);
            }

            return flag;
        }

        @Override
        public boolean hitBlock(World world, BlockPos pos, int tick) {
            return false;
        }

        @Override
        public boolean applyBuff(Entity victim, int tick, float force) {
            return ModBuff.applyBuff(victim, LibBuff.TAKEN_KING, tick, force, true);
        }

        @Override
        public boolean applyDebuff(Entity victim, int tick, float force) {

            if(victim instanceof MobEntity && ModBuff.hasBuff(victim, LibBuff.TAKEN))
            {
                ITakenState state = victim.getCapability(MagickCore.takenState).orElse(null);
                state.setOwner(victim.getUniqueID());
                state.setTime((int) (tick * force));

                return true;
            }
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
