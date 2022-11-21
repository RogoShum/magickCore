package com.rogoshum.magickcore.common.mixin;

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

    public MixinMobEntity(EntityType<?> entityTypeIn, World worldIn) {
        super(entityTypeIn, worldIn);
    }

    protected LivingEntity host;
    protected double range;

    @Shadow
    public abstract void setAttackTarget(LivingEntity livingEntity);

    @Shadow
    public abstract LivingEntity getAttackTarget();

    @Shadow
    public abstract PathNavigator getNavigator();

    @Inject(method = "tick", at = @At("HEAD"))
    public void onTick(CallbackInfo info) {
        LivingEntity entity = null;
        TakenEntityData taken = ExtraDataUtil.takenEntityData(this);
        if(taken != null && taken.getTime() > 0 && this.world instanceof ServerWorld) {
            range = taken.getRange();
            entity = (LivingEntity) ((ServerWorld)this.world).getEntityByUuid(taken.getOwnerUUID());
        }
        host = entity;
        if(host != null && getAttackTarget() == null && this.rand.nextInt(80) == 1) {
            if(host.getAttackingEntity() != null)
                setAttackTarget(host.getAttackingEntity());
            else
                getNavigator().tryMoveToEntityLiving(host, 1);
        }
    }

    @Inject(method = "getAttackTarget", at = @At("RETURN"), cancellable = true)
    public void onGetAttackTarget(CallbackInfoReturnable<LivingEntity> cir) {
        if(cir.getReturnValue() != null)
            cir.setReturnValue(TakenTargetUtil.decideChangeTarget(this, host, cir.getReturnValue(), this.range));
        else
            cir.setReturnValue(TakenTargetUtil.getTakenTarget(this, range));
    }

    @Inject(method = "setAttackTarget", at = @At("HEAD"))
    public void onSetAttackTarget(LivingEntity entitylivingbaseIn, CallbackInfo info) {
        if(entitylivingbaseIn != null)
            entitylivingbaseIn = TakenTargetUtil.decideChangeTarget(this, host, entitylivingbaseIn, this.range);
        else
            entitylivingbaseIn = TakenTargetUtil.getTakenTarget(this, range);
    }
}
