package com.rogoshum.magickcore.mixin;

import com.rogoshum.magickcore.api.event.EntityEvents;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

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
}
