package com.rogoshum.magickcore.common.util;

import com.rogoshum.magickcore.common.extradata.ExtraDataUtil;
import com.rogoshum.magickcore.common.extradata.entity.EntityStateData;
import com.rogoshum.magickcore.common.extradata.entity.TakenEntityData;
import com.rogoshum.magickcore.common.lib.LibBuff;
import com.rogoshum.magickcore.common.magick.MagickReleaseHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.world.server.ServerWorld;

import java.util.List;

public class TakenTargetUtil {
    public static LivingEntity getTakenTarget(Entity mob, double range) {
        List<Entity> list = mob.world.getEntitiesWithinAABBExcludingEntity(mob, mob.getBoundingBox().grow(range));
        if(!mob.world.isRemote) {
            TakenEntityData state = ExtraDataUtil.takenEntityData(mob);
            if(state != null && mob.world != null) {
                LivingEntity victim = null;
                LivingEntity owner = (LivingEntity) ((ServerWorld) mob.world).getEntityByUuid(state.getOwnerUUID());
                if(owner != null && owner != mob) {
                    for (Entity entity : list) {
                        TakenEntityData takenEntity = ExtraDataUtil.takenEntityData(entity);
                        boolean sameOwner = takenEntity == null || !takenEntity.getOwnerUUID().equals(state.getOwnerUUID());

                        if (entity instanceof LivingEntity && !MagickReleaseHelper.sameLikeOwner(owner, entity) && MagickReleaseHelper.canEntityTraceAnother(mob, entity) && sameOwner) {
                            if (victim == null || mob.getDistance(victim) > mob.getDistance(entity))
                                victim = (LivingEntity) entity;
                        }
                    }
                } else if(owner == mob)
                    return null;
                return victim;
            }
        }

        return null;
    }

    public static LivingEntity decideChangeTarget(Entity taken, LivingEntity host, LivingEntity target, double range) {
        if(host != null) {
            LivingEntity newTarget = target;
            if(host.getUniqueID().equals(target.getUniqueID()))
                newTarget = TakenTargetUtil.getTakenTarget(taken, range);
            else if(host.getUniqueID().equals(taken.getUniqueID()))
                newTarget = null;

            return newTarget;
        } else {
            EntityStateData state = ExtraDataUtil.entityStateData(taken);
            if(state != null && state.getBuffList().containsKey(LibBuff.TAKEN)) {
                return null;
            }
        }

        return target;
    }
}
