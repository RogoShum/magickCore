package com.rogoshum.magickcore.mixin;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.capability.ITakenState;
import com.rogoshum.magickcore.helper.MagickReleaseHelper;
import com.rogoshum.magickcore.helper.TakenTargetHelper;
import net.minecraft.client.world.ClientWorld;
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

import java.awt.*;
import java.util.UUID;

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
    public void onTick(CallbackInfo info)
    {
        LivingEntity entity = null;
        ITakenState taken = this.getCapability(MagickCore.takenState).orElse(null);
        if(taken != null && taken.getTime() > 0 && this.world instanceof ServerWorld)
        {
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
            cir.setReturnValue(TakenTargetHelper.decideChangeTarget(this, host, cir.getReturnValue(), this.range));
        else
            cir.setReturnValue(TakenTargetHelper.getTakenTarget(this, range));
    }

    @Inject(method = "setAttackTarget", at = @At("HEAD"))
    public void onSetAttackTarget(LivingEntity entitylivingbaseIn, CallbackInfo info) {
        if(entitylivingbaseIn != null)
            entitylivingbaseIn = TakenTargetHelper.decideChangeTarget(this, host, entitylivingbaseIn, this.range);
        else
            entitylivingbaseIn = TakenTargetHelper.getTakenTarget(this, range);
    }
}
