package com.rogoshum.magickcore.mixin;

import com.rogoshum.magickcore.common.extradata.entity.TakenEntityData;
import com.rogoshum.magickcore.common.util.TakenTargetUtil;
import com.rogoshum.magickcore.common.extradata.ExtraDataUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.pathfinding.PathNavigator;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MobEntity.class)
public abstract class MixinMobEntity extends Entity {
    @Shadow
    private LivingEntity target;

    public MixinMobEntity(EntityType<?> entityTypeIn, World worldIn) {
        super(entityTypeIn, worldIn);
    }

    protected LivingEntity host;
    protected double range;

    @Shadow
    public abstract void setTarget(LivingEntity livingEntity);

    @Shadow
    public abstract LivingEntity getTarget();

    @Shadow
    public abstract PathNavigator getNavigation();

    @Inject(method = "tick", at = @At("HEAD"))
    public void onTick(CallbackInfo info) {
        LivingEntity entity = null;
        TakenEntityData taken = ExtraDataUtil.takenEntityData(this);
        if(taken != null && taken.getTime() > 0 && this.level instanceof ServerWorld) {
            range = taken.getRange();
            entity = (LivingEntity) ((ServerWorld)this.level).getEntity(taken.getOwnerUUID());
        }
        host = entity;
        if(host != null && getTarget() == null && this.random.nextInt(80) == 1) {
            LivingEntity target = host.getLastHurtMob();
            if(target == null)
                target = host.getLastHurtByMob();
            if(target != null)
                setTarget(target);
            else
                getNavigation().moveTo(host, 1);
        }
    }

    @Inject(method = "getTarget", at = @At("RETURN"), cancellable = true)
    public void onGetAttackTarget(CallbackInfoReturnable<LivingEntity> cir) {
        if(cir.getReturnValue() != null) {
            target = TakenTargetUtil.decideChangeTarget(this, host, cir.getReturnValue(), this.range);
            cir.setReturnValue(target);
        } else {
            target = TakenTargetUtil.getTakenTarget(this, range);
            cir.setReturnValue(target);
        }
    }

/*
    @Inject(method = "setAttackTarget", at = @At("RETURN"))
    public void onSetAttackTarget(LivingEntity entitylivingbaseIn, CallbackInfo ci) {
        if(attackTarget != null)
            attackTarget = TakenTargetUtil.decideChangeTarget(this, host, attackTarget, this.range);
        else
            attackTarget = TakenTargetUtil.getTakenTarget(this, range);
    }
 */
}
