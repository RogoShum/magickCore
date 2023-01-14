package com.rogoshum.magickcore.mixin;

import com.rogoshum.magickcore.common.extradata.entity.TakenEntityData;
import com.rogoshum.magickcore.common.util.TakenTargetUtil;
import com.rogoshum.magickcore.common.extradata.ExtraDataUtil;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Mob.class)
public abstract class MixinMobEntity extends Entity {
    @Shadow
    private LivingEntity target;

    public MixinMobEntity(EntityType<?> entityTypeIn, Level worldIn) {
        super(entityTypeIn, worldIn);
    }

    protected LivingEntity host;
    protected double range;

    @Shadow
    public abstract void setTarget(LivingEntity livingEntity);

    @Shadow
    public abstract LivingEntity getTarget();

    @Shadow
    public abstract PathNavigation getNavigation();

    @Inject(method = "tick", at = @At("HEAD"))
    public void onTick(CallbackInfo info) {
        LivingEntity entity = null;
        TakenEntityData taken = ExtraDataUtil.takenEntityData(this);
        if(taken != null && taken.getTime() > 0 && this.level instanceof ServerLevel) {
            range = taken.getRange();
            entity = (LivingEntity) ((ServerLevel)this.level).getEntity(taken.getOwnerUUID());
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
