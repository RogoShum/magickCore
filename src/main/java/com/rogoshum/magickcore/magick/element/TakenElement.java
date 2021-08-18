package com.rogoshum.magickcore.magick.element;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.enums.EnumManaLimit;
import com.rogoshum.magickcore.capability.ITakenState;
import com.rogoshum.magickcore.init.ModBuff;
import com.rogoshum.magickcore.init.ModDamage;
import com.rogoshum.magickcore.lib.LibBuff;
import com.rogoshum.magickcore.magick.ReleaseAttribute;
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
        public boolean hitEntity(ReleaseAttribute attribute) {
            return ModBuff.applyBuff(attribute.victim, LibBuff.TAKEN, attribute.tick, attribute.force, true);
        }

        @Override
        public boolean damageEntity(ReleaseAttribute attribute) {
            if(ModBuff.hasBuff(attribute.victim, LibBuff.TAKEN))
                attribute.force *= 1.75;

            boolean flag = false;
            if(attribute.entity != null && attribute.projectile != null)
                flag = attribute.victim.attackEntityFrom(ModDamage.applyProjectileTakenDamage(attribute.entity, attribute.projectile), attribute.force);
            else if(attribute.entity != null)
                flag = attribute.victim.attackEntityFrom(ModDamage.applyEntityTakenDamage(attribute.entity), attribute.force);
            else if(attribute.projectile != null)
                flag = attribute.victim.attackEntityFrom(ModDamage.applyEntityTakenDamage(attribute.projectile), attribute.force);
            else
                flag = attribute.victim.attackEntityFrom(ModDamage.getTakenDamage(), attribute.force);

            if(flag && attribute.force >= EnumManaLimit.FORCE.getValue() * 1.75 && attribute.entity != null && attribute.victim instanceof MobEntity && ModBuff.hasBuff(attribute.victim, LibBuff.TAKEN))
            {
                ITakenState state = attribute.victim.getCapability(MagickCore.takenState).orElse(null);
                state.setOwner(attribute.entity.getUniqueID());
                state.setTime(attribute.tick);
                attribute.victim.playSound(SoundEvents.ENTITY_BLAZE_HURT, 2.0F, 0.0f);
            }

            return flag;
        }

        @Override
        public boolean hitBlock(World world, BlockPos pos, int tick) {
            return false;
        }

        @Override
        public boolean applyBuff(ReleaseAttribute attribute) {
            return ModBuff.applyBuff(attribute.victim, LibBuff.TAKEN_KING, attribute.tick, attribute.force, true);
        }

        @Override
        public boolean applyDebuff(ReleaseAttribute attribute) {

            if(attribute.victim instanceof MobEntity && ModBuff.hasBuff(attribute.victim, LibBuff.TAKEN))
            {
                ITakenState state = attribute.victim.getCapability(MagickCore.takenState).orElse(null);
                state.setOwner(attribute.victim.getUniqueID());
                state.setTime((int) (attribute.tick * attribute.force));

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
