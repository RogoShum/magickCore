package com.rogoshum.magickcore.common.mixin;

import com.rogoshum.magickcore.api.entity.ILightSourceEntity;
import com.rogoshum.magickcore.api.entity.IRedStoneEntity;
import com.rogoshum.magickcore.api.event.EntityEvents;
import com.rogoshum.magickcore.common.util.EntityLightSourceManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

@Mixin(World.class)
public abstract class MixinWorld {
    @Shadow public abstract List<Entity> getEntitiesInAABBexcluding(@Nullable Entity entityIn, AxisAlignedBB boundingBox, @Nullable Predicate<? super Entity> predicate);

    @Inject(method = "guardEntityTick", at = @At("HEAD"), cancellable = true)
    public void onGuardEntityTick(Consumer<Entity> consumerEntity, Entity entityIn, CallbackInfo info) {
        if(entityIn instanceof LivingEntity) return;
        EntityEvents.EntityUpdateEvent event = new EntityEvents.EntityUpdateEvent(entityIn);
        MinecraftForge.EVENT_BUS.post(event);
        if(event.isCanceled())
            info.cancel();
    }

    @Inject(method = "getRedstonePower", at = @At("RETURN"), cancellable = true)
    public void onGetRedstonePower(BlockPos pos, Direction facing, CallbackInfoReturnable<Integer> cir) {
        List<Entity> list = ((World) (Object)this).getEntitiesInAABBexcluding(null, new AxisAlignedBB(pos), entity -> entity instanceof IRedStoneEntity);
        if(!list.isEmpty()) {
            int power = 0;
            for(Entity entity : list) {
                if(pos.equals(new BlockPos(entity.getPositionVec())) && ((IRedStoneEntity) entity).getPower() > power)
                    power = ((IRedStoneEntity) entity).getPower();
            }
            if(power > cir.getReturnValue())
                cir.setReturnValue(power);
        }
    }
}
