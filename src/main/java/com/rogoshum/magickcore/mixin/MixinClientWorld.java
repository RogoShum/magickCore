package com.rogoshum.magickcore.mixin;

import com.rogoshum.magickcore.api.event.EntityEvents;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientWorld.class)
public class MixinClientWorld {

    @Inject(method = "addEntityImpl", at = @At(value = "TAIL"))
    public void onAddEntity(int entityIdIn, Entity entityToSpawn, CallbackInfo ci) {
        EntityEvents.EntityAddedToWorldEvent event = new EntityEvents.EntityAddedToWorldEvent(entityToSpawn);
        MinecraftForge.EVENT_BUS.post(event);
    }
}
