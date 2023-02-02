package com.rogoshum.magickcore.mixin.fabric;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.api.event.PlayerEvent;
import com.rogoshum.magickcore.api.event.living.LivingDeathEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayer.class)
public class MixinServerPlayer {
    @Inject(method = "restoreFrom", at = @At(value = "TAIL"))
    public void onDeath(ServerPlayer serverPlayer, boolean bl, CallbackInfo ci) {
        PlayerEvent.Clone clone = new PlayerEvent.Clone((Player) (Object)this, serverPlayer, bl);
        MagickCore.EVENT_BUS.post(clone);
    }
}
