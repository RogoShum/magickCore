package com.rogoshum.magickcore.tool;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.capability.ITakenState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.world.server.ServerWorld;

import java.util.List;

public class TakenTargetHelper {
    public static LivingEntity getTakenTarget(Entity mob, double range)
    {
        List<Entity> list = mob.world.getEntitiesWithinAABBExcludingEntity(mob, mob.getBoundingBox().grow(range));
        if(!mob.world.isRemote) {
            ITakenState state = mob.getCapability(MagickCore.takenState).orElse(null);
            if(state != null && mob.world != null) {
                LivingEntity victim = null;
                LivingEntity owner = (LivingEntity) ((ServerWorld) mob.world).getEntityByUuid(state.getOwnerUUID());
                if(owner != null && owner != mob) {
                    for (Entity entity : list) {
                        ITakenState takenEntity = entity.getCapability(MagickCore.takenState).orElse(null);
                        boolean sameOwner = false;
                        if (takenEntity == null || !takenEntity.getOwnerUUID().equals(state.getOwnerUUID()))
                            sameOwner = true;

                        if (entity instanceof LivingEntity && !MagickReleaseHelper.sameLikeOwner(owner, entity) && MagickReleaseHelper.canEntityTraceAnother(mob, entity) && sameOwner) {
                            if (victim == null || mob.getDistance(victim) > mob.getDistance(entity))
                                victim = (LivingEntity) entity;
                        }
                    }
                }
                return victim;
            }
        }

        return null;
    }

    public static LivingEntity decideChangeTarget(Entity taken, LivingEntity host, LivingEntity target, double range)
    {
        if(host != null)
        {
            LivingEntity newTarget = target;
            if(host.getUniqueID().equals(target.getUniqueID()))
                newTarget = TakenTargetHelper.getTakenTarget(taken, range);
            else if(host.getUniqueID().equals(taken.getUniqueID()))
                newTarget = null;

            return newTarget;
        }

        return target;
    }
}
