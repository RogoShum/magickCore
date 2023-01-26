package com.rogoshum.magickcore.mixin;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.api.entity.IRedStoneEntity;
import com.rogoshum.magickcore.api.event.EntityEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.function.Consumer;

@Mixin(Level.class)
public abstract class MixinLevel {
    @Inject(method = "guardEntityTick", at = @At("HEAD"), cancellable = true)
    public void onGuardEntityTick(Consumer<Entity> consumerEntity, Entity entityIn, CallbackInfo info) {
        if(entityIn instanceof LivingEntity) return;
        EntityEvent.EntityUpdateEvent event = new EntityEvent.EntityUpdateEvent(entityIn);
        MagickCore.EVENT_BUS.post(event);
        if(event.isCanceled())
            info.cancel();
    }

    @Inject(method = "getSignal", at = @At("RETURN"), cancellable = true)
    public void onGetRedstonePower(BlockPos pos, Direction facing, CallbackInfoReturnable<Integer> cir) {
        List<Entity> list = ((Level) (Object)this).getEntities((Entity) null, new AABB(pos), entity -> entity instanceof IRedStoneEntity);
        if(!list.isEmpty()) {
            int power = 0;
            for(Entity entity : list) {
                if(pos.equals(new BlockPos(entity.position())) && ((IRedStoneEntity) entity).getPower() > power)
                    power = ((IRedStoneEntity) entity).getPower();
            }
            if(power > cir.getReturnValue())
                cir.setReturnValue(power);
        }
    }
}
