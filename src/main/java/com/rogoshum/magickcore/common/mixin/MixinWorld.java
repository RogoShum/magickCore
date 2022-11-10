package com.rogoshum.magickcore.common.mixin;

import com.rogoshum.magickcore.common.api.entity.ILightSourceEntity;
import com.rogoshum.magickcore.common.api.entity.IRedStoneEntity;
import com.rogoshum.magickcore.common.api.event.EntityEvents;
import com.rogoshum.magickcore.common.util.EntityLightSourceManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Consumer;

@Mixin(World.class)
public class MixinWorld {
    @Inject(method = "guardEntityTick", at = @At("HEAD"), cancellable = true)
    public void onGuardEntityTick(Consumer<Entity> consumerEntity, Entity entityIn, CallbackInfo info) {
        EntityEvents.EntityUpdateEvent event = new EntityEvents.EntityUpdateEvent(entityIn);
        MinecraftForge.EVENT_BUS.post(event);
        if(event.isCanceled())
            info.cancel();
    }

    @Inject(method = "getRedstonePower", at = @At("RETURN"), cancellable = true)
    public void onGetRedstonePower(BlockPos pos, Direction facing, CallbackInfoReturnable<Integer> cir) {
        ILightSourceEntity entity = EntityLightSourceManager.getPosLighting((World) (Object)this, pos);
        if(entity instanceof IRedStoneEntity)
            cir.setReturnValue(((IRedStoneEntity) entity).getPower());
    }
}
