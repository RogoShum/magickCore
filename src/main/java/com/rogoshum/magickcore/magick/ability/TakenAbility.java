package com.rogoshum.magickcore.magick.ability;

import com.rogoshum.magickcore.enums.EnumManaLimit;
import com.rogoshum.magickcore.init.ModBuff;
import com.rogoshum.magickcore.init.ModDamage;
import com.rogoshum.magickcore.lib.LibBuff;
import com.rogoshum.magickcore.magick.context.MagickContext;
import com.rogoshum.magickcore.magick.extradata.entity.TakenEntityData;
import com.rogoshum.magickcore.tool.ExtraDataHelper;
import net.minecraft.entity.MobEntity;
import net.minecraft.util.SoundEvents;

public class TakenAbility{
    public static boolean hitEntity(MagickContext attribute) {
        return ModBuff.applyBuff(attribute.victim, LibBuff.TAKEN, attribute.tick, attribute.force, true);
    }

    public static boolean damageEntity(MagickContext attribute) {
        if(attribute.victim == null) return false;
        if(ModBuff.hasBuff(attribute.victim, LibBuff.TAKEN))
            attribute.force *= 1.75;

        boolean flag = false;
        if(attribute.caster != null && attribute.projectile != null)
            flag = attribute.victim.attackEntityFrom(ModDamage.applyProjectileTakenDamage(attribute.caster, attribute.projectile), attribute.force);
        else if(attribute.caster != null)
            flag = attribute.victim.attackEntityFrom(ModDamage.applyEntityTakenDamage(attribute.caster), attribute.force);
        else if(attribute.projectile != null)
            flag = attribute.victim.attackEntityFrom(ModDamage.applyEntityTakenDamage(attribute.projectile), attribute.force);
        else
            flag = attribute.victim.attackEntityFrom(ModDamage.getTakenDamage(), attribute.force);

        if(flag && attribute.force >= EnumManaLimit.FORCE.getValue() * 1.75 && attribute.caster != null && attribute.victim instanceof MobEntity && ModBuff.hasBuff(attribute.victim, LibBuff.TAKEN)) {
            TakenEntityData state = ExtraDataHelper.takenEntityData(attribute.victim);
            state.setOwner(attribute.caster.getUniqueID());
            state.setTime(attribute.tick);
            attribute.victim.playSound(SoundEvents.ENTITY_BLAZE_HURT, 2.0F, 0.0f);
        }

        return flag;
    }

    public static boolean hitBlock(MagickContext attribute) {
        return false;
    }

    public static boolean applyBuff(MagickContext attribute) {
        return ModBuff.applyBuff(attribute.victim, LibBuff.TAKEN_KING, attribute.tick, attribute.force, true);
    }

    public static boolean applyDebuff(MagickContext attribute) {
        if(attribute.victim instanceof MobEntity && ModBuff.hasBuff(attribute.victim, LibBuff.TAKEN)) {
            TakenEntityData state = ExtraDataHelper.takenEntityData(attribute.victim);
            state.setOwner(attribute.victim.getUniqueID());
            state.setTime((int) (attribute.tick * attribute.force));

            return true;
        }
        return false;
    }

    public static void applyToolElement(MagickContext context) {

    }
}
