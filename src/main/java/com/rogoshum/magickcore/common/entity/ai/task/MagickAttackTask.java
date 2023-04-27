package com.rogoshum.magickcore.common.entity.ai.task;

import com.google.common.collect.ImmutableMap;
import com.rogoshum.magickcore.api.entity.IManaTaskMob;

import com.rogoshum.magickcore.api.magick.MagickReleaseHelper;
import com.rogoshum.magickcore.api.magick.context.MagickContext;
import com.rogoshum.magickcore.api.magick.context.child.ConditionContext;
import com.rogoshum.magickcore.common.lib.LibContext;
import com.rogoshum.magickcore.api.magick.context.SpellContext;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.BehaviorUtils;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.InteractionHand;
import net.minecraft.server.level.ServerLevel;

import java.util.Queue;

public class MagickAttackTask<T extends LivingEntity> extends Behavior<T> {
    private final int cooldown;
    private final int range;

    public MagickAttackTask(int cooldown, int range) {
        super(ImmutableMap.of(MemoryModuleType.LOOK_TARGET, MemoryStatus.REGISTERED, MemoryModuleType.ATTACK_TARGET, MemoryStatus.REGISTERED, MemoryModuleType.HURT_BY_ENTITY, MemoryStatus.REGISTERED, MemoryModuleType.ATTACK_COOLING_DOWN, MemoryStatus.VALUE_ABSENT));
        this.cooldown = cooldown;
        this.range = range;
    }

    protected boolean checkExtraStartConditions(ServerLevel worldIn, T owner) {
        if(!(owner instanceof IManaTaskMob)) return false;
        boolean hasFight = ((IManaTaskMob) owner).conditionSpellMap().containsKey(Activity.FIGHT);
        if(!hasFight) return false;

        LivingEntity livingentity = this.getAttackTarget(owner);
        if(livingentity == null) return false;
        return owner.distanceToSqr(livingentity) <= range * range && BehaviorUtils.canSee(owner, livingentity);
    }

    protected void start(ServerLevel worldIn, T entityIn, long gameTimeIn) {
        if(!(entityIn instanceof IManaTaskMob)) return;
        LivingEntity livingentity = this.getAttackTarget(entityIn);
        if(livingentity == null) return;
        BehaviorUtils.lookAtEntity(entityIn, livingentity);
        entityIn.swing(InteractionHand.MAIN_HAND);
        Queue<SpellContext> contextQueue = ((IManaTaskMob) entityIn).conditionSpellMap().get(Activity.REST);
        for (SpellContext context : contextQueue) {
            if (!context.containChild(LibContext.CONDITION)) continue;
            ConditionContext condition = context.getChild(LibContext.CONDITION);
            if (condition.test(entityIn, entityIn)) {
                MagickContext magickContext = MagickContext.create(entityIn.level, context).caster(entityIn).victim(entityIn).noCost();
                if (MagickReleaseHelper.releaseMagick(magickContext))
                    return;
            }
        }

        contextQueue = ((IManaTaskMob) entityIn).conditionSpellMap().get(Activity.FIGHT);
        entityIn.getBrain().setMemoryWithExpiry(MemoryModuleType.ATTACK_COOLING_DOWN, true, (long)this.cooldown);
        for (SpellContext context : contextQueue) {
            if (!context.containChild(LibContext.CONDITION)) continue;
            ConditionContext condition = context.getChild(LibContext.CONDITION);
            if (condition.test(entityIn, livingentity)) {
                MagickContext magickContext = MagickContext.create(entityIn.level, context).caster(entityIn).victim(livingentity).noCost();
                if (MagickReleaseHelper.releaseMagick(magickContext))
                    return;
            }
        }
    }

    private LivingEntity getAttackTarget(T mob) {
        if(mob.getBrain().getMemory(MemoryModuleType.HURT_BY_ENTITY).isPresent())
            return mob.getBrain().getMemory(MemoryModuleType.HURT_BY_ENTITY).get();
        if(mob.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET).isPresent())
            return mob.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET).get();
        return null;
    }
}
