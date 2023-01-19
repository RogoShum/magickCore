package com.rogoshum.magickcore.mixin;

import com.rogoshum.magickcore.api.event.EntityEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.resources.sounds.EntityBoundSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientLevel.class)
public class MixinClientWorld {

    @Inject(method = "addEntity", at = @At(value = "TAIL"))
    public void onAddEntity(int entityIdIn, Entity entityToSpawn, CallbackInfo ci) {
        EntityEvent.EntityAddedToWorldEvent event = new EntityEvent.EntityAddedToWorldEvent(entityToSpawn);
        MinecraftForge.EVENT_BUS.post(event);
    }

    @Redirect(method = "playSound(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/sounds/SoundEvent;Lnet/minecraft/sounds/SoundSource;FF)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/sounds/SoundManager;play(Lnet/minecraft/client/resources/sounds/SoundInstance;)V"))
    public void onMovingSound(SoundManager instance, SoundInstance soundInstance) {
    }

    @Inject(method = "playSound(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/sounds/SoundEvent;Lnet/minecraft/sounds/SoundSource;FF)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/sounds/SoundManager;play(Lnet/minecraft/client/resources/sounds/SoundInstance;)V"))
    public void onMovingSound(Player playerIn, Entity entityIn, SoundEvent eventIn, SoundSource categoryIn, float volume, float pitch, CallbackInfo ci) {
        Minecraft.getInstance().getSoundManager().play(new EntityBoundSoundInstance(eventIn, categoryIn, volume, pitch, entityIn));
    }
}
