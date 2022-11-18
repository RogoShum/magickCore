package com.rogoshum.magickcore.common.entity.ai.task;

import com.google.common.collect.ImmutableMap;
import com.rogoshum.magickcore.common.api.entity.IManaTaskMob;

import com.rogoshum.magickcore.common.magick.MagickReleaseHelper;
import com.rogoshum.magickcore.common.magick.context.MagickContext;
import com.rogoshum.magickcore.common.magick.context.child.ConditionContext;
import com.rogoshum.magickcore.common.lib.LibContext;
import com.rogoshum.magickcore.common.magick.context.SpellContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.BrainUtil;
import net.minecraft.entity.ai.brain.memory.MemoryModuleStatus;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.ai.brain.schedule.Activity;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.util.Hand;
import net.minecraft.world.server.ServerWorld;

import java.util.Queue;

public class MagickAttackTask<T extends LivingEntity> extends Task<T> {
    private final int cooldown;
    private final int range;

    public MagickAttackTask(int cooldown, int range) {
        super(ImmutableMap.of(MemoryModuleType.LOOK_TARGET, MemoryModuleStatus.REGISTERED, MemoryModuleType.ATTACK_TARGET, MemoryModuleStatus.REGISTERED, MemoryModuleType.HURT_BY_ENTITY, MemoryModuleStatus.REGISTERED, MemoryModuleType.ATTACK_COOLING_DOWN, MemoryModuleStatus.VALUE_ABSENT));
        this.cooldown = cooldown;
        this.range = range;
    }

    protected boolean shouldExecute(ServerWorld worldIn, T owner) {
        if(!(owner instanceof IManaTaskMob)) return false;
        boolean hasFight = ((IManaTaskMob) owner).conditionSpellMap().containsKey(Activity.FIGHT);
        if(!hasFight) return false;

        LivingEntity livingentity = this.getAttackTarget(owner);
        if(livingentity == null) return false;
        return owner.getDistanceSq(livingentity) <= range * range && BrainUtil.isMobVisible(owner, livingentity);
    }

    protected void startExecuting(ServerWorld worldIn, T entityIn, long gameTimeIn) {
        if(!(entityIn instanceof IManaTaskMob)) return;
        LivingEntity livingentity = this.getAttackTarget(entityIn);
        if(livingentity == null) return;
        BrainUtil.lookAt(entityIn, livingentity);
        entityIn.swingArm(Hand.MAIN_HAND);
        Queue<SpellContext> contextQueue = ((IManaTaskMob) entityIn).conditionSpellMap().get(Activity.REST);
        for (SpellContext context : contextQueue) {
            if (!context.containChild(LibContext.CONDITION)) continue;
            ConditionContext condition = context.getChild(LibContext.CONDITION);
            if (condition.test(entityIn, entityIn)) {
                MagickContext magickContext = MagickContext.create(entityIn.world, context).caster(entityIn).victim(entityIn).noCost();
                if (MagickReleaseHelper.releaseMagick(magickContext))
                    return;
            }
        }

        contextQueue = ((IManaTaskMob) entityIn).conditionSpellMap().get(Activity.FIGHT);
        entityIn.getBrain().replaceMemory(MemoryModuleType.ATTACK_COOLING_DOWN, true, (long)this.cooldown);
        for (SpellContext context : contextQueue) {
            if (!context.containChild(LibContext.CONDITION)) continue;
            ConditionContext condition = context.getChild(LibContext.CONDITION);
            if (condition.test(entityIn, livingentity)) {
                MagickContext magickContext = MagickContext.create(entityIn.world, context).caster(entityIn).victim(livingentity).noCost();
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
