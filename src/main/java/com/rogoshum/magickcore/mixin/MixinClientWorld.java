package com.rogoshum.magickcore.mixin;

import com.rogoshum.magickcore.api.event.EntityEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.EntityTickableSound;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientWorld.class)
public class MixinClientWorld {

    @Inject(method = "addEntityImpl", at = @At(value = "TAIL"))
    public void onAddEntity(int entityIdIn, Entity entityToSpawn, CallbackInfo ci) {
        EntityEvents.EntityAddedToWorldEvent event = new EntityEvents.EntityAddedToWorldEvent(entityToSpawn);
        MinecraftForge.EVENT_BUS.post(event);
    }

    @Redirect(method = "playMovingSound", at = @At(value = "INVOKE", target = "net/minecraft/client/audio/SoundHandler.play (Lnet/minecraft/client/audio/ISound;)V"))
    public void onMovingSound(SoundHandler instance, ISound sound) {
    }

    @Inject(method = "playMovingSound", at = @At(value = "INVOKE", target = "net/minecraft/client/audio/SoundHandler.play (Lnet/minecraft/client/audio/ISound;)V"))
    public void onMovingSound(PlayerEntity playerIn, Entity entityIn, SoundEvent eventIn, SoundCategory categoryIn, float volume, float pitch, CallbackInfo ci) {
        Minecraft.getInstance().getSoundHandler().play(new EntityTickableSound(eventIn, categoryIn, volume, pitch, entityIn));
    }
}
